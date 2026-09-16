# Transmutation shop prototype (Fabric 26.2)

This project keeps the ProjectE-style table/tablet assets and side-mountable table. The table and tablet open one shop screen. EMC is a server-owned player account saved in the overworld's `transmutation_accounts` saved data. Clicking a valued stack in the screen's inventory grid sells it, teaches its item ID, and credits its fixed EMC value. Clicking a learned item in the right transmutation circle buys it. The screen has search and learned-item pages.

This is a focused prototype rather than a full ProjectE port. The fixed-value list is currently in `PETransmutationState.java` and includes cobblestone (1), dirt (1), iron (256), diamond (8192), and a handful of other vanilla items. Unvalued items are not sold or learned. It uses item IDs rather than component-aware item variants. Selling consumes the selected inventory stack up to 64; purchases produce one item. There is no recipe-derived EMC mapping yet.

The screen now renders the original table texture with right-circle learned outputs and a 36-slot inventory grid. It is still a prototype: the left input circle is decorative, and selling uses clicks in the lower inventory grid rather than cursor-slot interactions. Search and page behavior, tablet behavior, persistence across relogs, and multiplayer accounts still need in-game checks before publishing a jar.

Build on a machine with Java 25 and access to Fabric Maven: `./gradlew build` (PowerShell: `./gradlew.bat build`). Then test a new and existing world: side-place the table, sell and buy cobblestone, relog and confirm EMC/knowledge remain, try the tablet, and have two players confirm accounts remain separate.

Source references: Universal Shops 1.15.1 (26.2 Fabric shop API patterns), ProjectEF 1.21.1 (Transmutation behavior), and the supplied 26.2 ProjectE shell/assets. Refer to bundled LICENSE and original project attribution for retained assets.
