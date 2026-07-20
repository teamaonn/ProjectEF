package moze_intel.projecte.gameObjs.registration;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A lazily bound holder for a registry object. Mirrors the behavior the NeoForge DeferredHolder used to provide: created up front, bound when the owning
 * {@link PEDeferredRegister} performs the actual registration against the vanilla registry.
 */
public class PEDeferredHolder<R, T extends R> implements Holder<R>, INamedEntry, Supplier<T> {

	private final ResourceKey<R> key;
	@Nullable
	private T value;
	@Nullable
	private Holder.Reference<R> reference;

	public PEDeferredHolder(ResourceKey<? extends Registry<R>> registryKey, ResourceLocation valueName) {
		this(ResourceKey.create(registryKey, valueName));
	}

	public PEDeferredHolder(ResourceKey<R> key) {
		this.key = key;
	}

	/**
	 * Binds this holder to the registered value. Called by {@link PEDeferredRegister} when registration is performed.
	 */
	void bind(T value, Holder.Reference<R> reference) {
		this.value = value;
		this.reference = reference;
	}

	protected Holder.Reference<R> boundReference() {
		if (reference == null) {
			throw new IllegalStateException("Registry object not present: " + key + ". Was the owning register's register() method called yet?");
		}
		return reference;
	}

	/**
	 * @return The registered value.
	 */
	@Override
	public T get() {
		return value();
	}

	@NotNull
	@Override
	public T value() {
		if (value == null) {
			throw new IllegalStateException("Registry object not present: " + key + ". Was the owning register's register() method called yet?");
		}
		return value;
	}

	public ResourceLocation getId() {
		return key.location();
	}

	public ResourceKey<R> getKey() {
		return key;
	}

	public Optional<T> asOptional() {
		return Optional.ofNullable(value);
	}

	@Override
	public String getName() {
		return getId().getPath();
	}

	@Override
	public boolean isBound() {
		return value != null;
	}

	@Override
	public boolean is(@NotNull ResourceLocation id) {
		return id.equals(key.location());
	}

	@Override
	public boolean is(@NotNull ResourceKey<R> key) {
		return key == this.key;
	}

	@Override
	public boolean is(@NotNull Predicate<ResourceKey<R>> predicate) {
		return predicate.test(key);
	}

	@Override
	public boolean is(@NotNull TagKey<R> tag) {
		return boundReference().is(tag);
	}

	@Override
	public boolean is(@NotNull Holder<R> holder) {
		return holder.is(key.location());
	}

	@NotNull
	@Override
	public Stream<TagKey<R>> tags() {
		return boundReference().tags();
	}

	@NotNull
	@Override
	public Either<ResourceKey<R>, R> unwrap() {
		return Either.left(key);
	}

	@NotNull
	@Override
	public Optional<ResourceKey<R>> unwrapKey() {
		return Optional.of(key);
	}

	@NotNull
	@Override
	public Kind kind() {
		return Kind.REFERENCE;
	}

	@Override
	public boolean canSerializeIn(@NotNull HolderOwner<R> owner) {
		return reference != null && reference.canSerializeIn(owner);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof PEDeferredHolder<?, ?> other && other.key == this.key;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public String toString() {
		return "PEDeferredHolder{" + key + "}";
	}
}
