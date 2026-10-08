"""Build conservative vanilla EMC values from ProjectEF defaults and 26.2 recipes.

Usage: python3 tools/generate_emc_values.py /path/to/server-26.2.jar
The output is a fixed registry; runtime sales and purchases use identical prices.
"""
import json
import pathlib
import sys
import zipfile

ROOT = pathlib.Path(__file__).resolve().parents[1]
DEFAULTS = ROOT / "references" / "projectef-defaults.json"
OUTPUT = ROOT / "src/main/resources/data/projecte/emc_values.json"

SEEDS = {
    "minecraft:oak_log": 32, "minecraft:birch_log": 32, "minecraft:spruce_log": 32,
    "minecraft:jungle_log": 32, "minecraft:acacia_log": 32, "minecraft:dark_oak_log": 32,
    "minecraft:mangrove_log": 32, "minecraft:cherry_log": 32, "minecraft:bamboo": 8,
    "minecraft:coal": 128, "minecraft:charcoal": 32, "minecraft:iron_ingot": 256,
    "minecraft:gold_ingot": 2048, "minecraft:redstone": 64, "minecraft:lapis_lazuli": 864,
    "minecraft:diamond": 8192, "minecraft:emerald": 16384,
    "minecraft:copper_ingot": 128, "minecraft:quartz": 256,
    "minecraft:raw_iron": 256, "minecraft:raw_gold": 2048,
    "minecraft:raw_copper": 128, "minecraft:obsidian": 64,
    "minecraft:flint": 4, "minecraft:clay_ball": 16,
    "minecraft:leather": 64, "minecraft:feather": 48,
    "minecraft:string": 12, "minecraft:wheat": 24, "minecraft:sugar_cane": 32,
    "minecraft:bone": 144, "minecraft:gunpowder": 192,
    "minecraft:rotten_flesh": 24, "minecraft:spider_eye": 128,
    "minecraft:blaze_rod": 1536, "minecraft:ender_pearl": 1024,
}

def excluded(item):
    name = item.split(":")[-1]
    return name.endswith(("_shulker_box",)) or name in {
        "bundle", "shulker_box", "enchanted_book", "written_book",
        "filled_map", "firework_rocket", "firework_star", "goat_horn", "player_head",
        "decorated_pot", "ominous_bottle",
    }

def main():
    default = json.loads(DEFAULTS.read_text())["values"]["before"]
    values = {e["id"]: e["emc_value"] for e in default if e.get("type") == "projecte:item"
              and isinstance(e.get("emc_value"), int) and e.get("id", "").startswith("minecraft:")}
    values.update(SEEDS)
    with zipfile.ZipFile(sys.argv[1]) as archive:
        tags = {}
        recipes = []
        ore_loot = {}
        for path in archive.namelist():
            if path.startswith("data/minecraft/tags/item/") and path.endswith(".json"):
                tags["minecraft:" + path[len("data/minecraft/tags/item/"):-5]] = (
                    json.loads(archive.read(path)).get("values", []))
            elif path.startswith("data/minecraft/recipe/") and path.endswith(".json"):
                recipes.append(json.loads(archive.read(path)))
            elif path.startswith("data/minecraft/loot_table/blocks/") and path.endswith("_ore.json"):
                ore_loot["minecraft:" + path.split("/")[-1][:-5]] = json.loads(archive.read(path))

    def ingredient_price(ingredient, seen=frozenset()):
        if isinstance(ingredient, list):
            options = [ingredient_price(part, seen) for part in ingredient]
            return min((v for v in options if v), default=0)
        if isinstance(ingredient, dict):
            ingredient = ingredient.get("item") or ("#" + ingredient["tag"] if "tag" in ingredient else None)
        if not isinstance(ingredient, str):
            return 0
        if ingredient.startswith("#"):
            tag = ingredient[1:]
            if tag in seen:
                return 0
            return ingredient_price(tags.get(tag, []), seen | {tag})
        return values.get(ingredient, 0)

    for _ in range(12):
        changes = 0
        for recipe in recipes:
            result = recipe.get("result", {})
            if isinstance(result, str):
                result = {"id": result}
            item = result.get("id") or result.get("item")
            if not item or excluded(item):
                continue
            count = max(1, result.get("count", 1))
            kind = recipe.get("type", "")
            if kind == "minecraft:crafting_shaped":
                parts = [recipe.get("key", {}).get(letter) for row in recipe.get("pattern", [])
                         for letter in row if letter != " "]
            elif kind == "minecraft:crafting_shapeless":
                parts = recipe.get("ingredients", [])
            elif kind in ("minecraft:smelting", "minecraft:blasting", "minecraft:smoking",
                          "minecraft:campfire_cooking", "minecraft:stonecutting"):
                parts = [recipe.get("ingredient")]
            elif kind == "minecraft:smithing_transform":
                parts = [recipe.get("template"), recipe.get("base"), recipe.get("addition")]
            else:
                continue
            costs = [ingredient_price(part) for part in parts]
            if not costs or any(cost == 0 for cost in costs):
                continue
            candidate = max(1, sum(costs) // count)
            if item not in values or candidate < values[item]:
                values[item] = candidate
                changes += 1
        if not changes:
            break
    # Base yield of a mined ore, ignoring Silk Touch, Fortune, and explosion loss.
    # Floor fractional EMC to keep the price an integer (e.g. redstone: 4.5 * 64 = 288).
    for ore, loot in ore_loot.items():
        for pool in loot.get("pools", []):
            for entry in pool.get("entries", []):
                children = entry.get("children", [])
                normal = next((child for child in children if child.get("name") != ore), None)
                if normal is None:
                    continue
                drop_price = values.get(normal.get("name"), 0)
                if not drop_price:
                    continue
                yield_count = 1
                for function in normal.get("functions", []):
                    if function.get("function") == "minecraft:set_count":
                        amount = function.get("count", {})
                        if amount.get("type") == "minecraft:uniform":
                            yield_count = (amount["min"] + amount["max"]) / 2
                        elif isinstance(amount, (int, float)):
                            yield_count = amount
                values[ore] = max(1, int(yield_count * drop_price))
    values = {item: amount for item, amount in values.items() if amount > 0 and not excluded(item)}
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text(json.dumps(dict(sorted(values.items())), indent=2) + "\n")
    print(f"{len(values)} priced vanilla items")

if __name__ == "__main__":
    main()
