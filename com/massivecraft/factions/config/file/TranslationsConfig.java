/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config.file;

import com.massivecraft.factions.config.annotation.Comment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TranslationsConfig {
    @Comment(value="This config file will slowly become the location for all text content\nAll information here uses MiniMessage. https://docs.adventure.kyori.net/minimessage.html")
    private Commands commands = new Commands();
    private Permissions permissions = new Permissions();

    public Commands commands() {
        return this.commands;
    }

    public Permissions permissions() {
        return this.permissions;
    }

    public static class Commands {
        private Permissions permissions = new Permissions();

        public Permissions permissions() {
            return this.permissions;
        }

        public static class Permissions
        extends AbsCommand {
            private SubCmdAdd add = new SubCmdAdd();
            private SubCmdList list = new SubCmdList();
            private SubCmdListOverride listOverride = new SubCmdListOverride();
            private SubCmdMove move = new SubCmdMove();
            private SubCmdRemove remove = new SubCmdRemove();
            private SubCmdReset reset = new SubCmdReset();
            private SubCmdShow show = new SubCmdShow();
            private SubCmdShowOverride showOverride = new SubCmdShowOverride();

            protected Permissions() {
                super("perm", "perms", "permission", "permissions");
            }

            public SubCmdAdd add() {
                return this.add;
            }

            public SubCmdList list() {
                return this.list;
            }

            public SubCmdListOverride listOverride() {
                return this.listOverride;
            }

            public SubCmdMove move() {
                return this.move;
            }

            public SubCmdRemove remove() {
                return this.remove;
            }

            public SubCmdReset reset() {
                return this.reset;
            }

            public SubCmdShow show() {
                return this.show;
            }

            public SubCmdShowOverride showOverride() {
                return this.showOverride;
            }

            public static class SubCmdAdd
            extends AbsCommand {
                private String availableSelectorsIntro = "Available: ";
                private String availableSelectorsSelector = "<click:run_command:\"<command>\"><color:#66ebff><selector></color:#66ebff></click>  ";
                private String selectorNotFound = "<red>No selector available with that name</red>";
                private String selectorCreateFail = "<red>Could not create selector:</red> <error>";
                private String selectorOptionHere = "OPTIONHERE";
                private String selectorOptionsIntro = "Available: ";
                private String selectorOptionsItem = "<click:run_command:\"<command>\"><color:#66ebff><display></color:#66ebff></click>  ";
                private String actionOptionsIntro = "Available: ";
                private String actionOptionsItem = "<hover:show_text:\"<description>\"><action></hover><click:run_command:\"<commandtrue>\"><color:#66ffb0>+</color:#66ffb0></click><click:run_command:\"<commandfalse>\"><color:#ff6666>-</color:#ff6666></click>  ";
                private String actionNotFound = "<red>No action available with that name</red>";
                private String actionAllowDenyOptions = "<red>Unrecognized choice. What about </red><allow> <red>or</red> <deny><red>?</red>";
                private List<String> actionAllowAlias = new ArrayList<String>(){
                    {
                        this.add("allow");
                        this.add("true");
                    }
                };
                private List<String> actionDenyAlias = new ArrayList<String>(){
                    {
                        this.add("deny");
                        this.add("false");
                        this.add("disallow");
                    }
                };

                public SubCmdAdd() {
                    super("add", new String[0]);
                }

                public String getAvailableSelectorsIntro() {
                    return this.availableSelectorsIntro;
                }

                public String getAvailableSelectorsSelector() {
                    return this.availableSelectorsSelector;
                }

                public String getSelectorCreateFail() {
                    return this.selectorCreateFail;
                }

                public String getSelectorNotFound() {
                    return this.selectorNotFound;
                }

                public String getSelectorOptionsIntro() {
                    return this.selectorOptionsIntro;
                }

                public String getSelectorOptionsItem() {
                    return this.selectorOptionsItem;
                }

                public String getSelectorOptionHere() {
                    return this.selectorOptionHere;
                }

                public String getActionOptionsIntro() {
                    return this.actionOptionsIntro;
                }

                public String getActionOptionsItem() {
                    return this.actionOptionsItem;
                }

                public String getActionNotFound() {
                    return this.actionNotFound;
                }

                public String getActionAllowDenyOptions() {
                    return this.actionAllowDenyOptions;
                }

                public List<String> getActionAllowAlias() {
                    return this.actionAllowAlias;
                }

                public List<String> getActionDenyAlias() {
                    return this.actionDenyAlias;
                }
            }

            public static class SubCmdList
            extends AbsCommand {
                private String footer = "           <click:run_command:\"<commandadd>\"><color:#66ebff>[Add]";
                private String header = "Selectors (ranked by priority) [<click:run_command:\"<commandoverride>\"><color:#66ebff>Overrides</color:#66ebff></click>]:";
                private String item = "<hover:show_text:\"<color:#ff6666>Move up\"><click:run_command:\"<commandmoveup>\">^</click></hover> <hover:show_text:\"<color:#ff6666>Move down\"><click:run_command:\"<commandmovedown>\">V</click></hover> <hover:show_text:\"<color:#ff6666>Remove\"><click:run_command:\"<commandremove>\"><color:#ff6666>X</color:#ff6666></click></hover> #<rownumber> <hover:show_text:\"<color:#ff6666>Click to show actions\"><click:run_command:\"<commandshow>\"><color:#66ebff><name></color:#66ebff></click></hover>: <color:#66ffb0><value>";

                public SubCmdList() {
                    super("list", new String[0]);
                }

                public String getFooter() {
                    return this.footer;
                }

                public String getHeader() {
                    return this.header;
                }

                public String getItem() {
                    return this.item;
                }
            }

            public static class SubCmdListOverride
            extends AbsCommand {
                private String footer = "";
                private String header = "Server Override Selectors (ranked by priority):";
                private String item = "#<rownumber> <hover:show_text:\"<color:#ff6666>Click to show actions\"><click:run_command:\"<commandshow>\"><color:#66ebff><name></color:#66ebff></click></hover>: <color:#66ffb0><value>";

                public SubCmdListOverride() {
                    super("listoverride", new String[0]);
                }

                public String getFooter() {
                    return this.footer;
                }

                public String getHeader() {
                    return this.header;
                }

                public String getItem() {
                    return this.item;
                }
            }

            public static class SubCmdMove
            extends AbsCommand {
                private List<String> aliasUp = new ArrayList<String>(){
                    {
                        this.add("up");
                    }
                };
                private List<String> aliasDown = new ArrayList<String>(){
                    {
                        this.add("down");
                    }
                };
                private String errorOptions = "<red>Unrecognized choice. What about </red><up> <red>or</red> <down><red>?</red>";
                private String errorHighest = "<red>Cannot move highest selector any higher!</red>";
                private String errorLowest = "<red>Cannot move lowest selector any lower!</red>";

                public SubCmdMove() {
                    super("move", new String[0]);
                }

                public List<String> getAliasUp() {
                    return this.aliasUp;
                }

                public List<String> getAliasDown() {
                    return this.aliasDown;
                }

                public String getErrorOptions() {
                    return this.errorOptions;
                }

                public String getErrorHighest() {
                    return this.errorHighest;
                }

                public String getErrorLowest() {
                    return this.errorLowest;
                }
            }

            public static class SubCmdRemove
            extends AbsCommand {
                public SubCmdRemove() {
                    super("remove", new String[0]);
                }
            }

            public static class SubCmdReset
            extends AbsCommand {
                private String warning = "<#ff6666>Warning:</#ff6666> Are you sure you wish to reset all permissions? <click:run_command:\"<command>\"><#ff6666>[CONFIRM]</#ff6666></click>";
                private String resetComplete = "<color:#66ebff>Permissions reset!";
                @Comment(value="must be a single word, to confirm resetting")
                private String confirmWord = "confirm";

                public SubCmdReset() {
                    super("reset", new String[0]);
                }

                public String getConfirmWord() {
                    return this.confirmWord;
                }

                public String getWarning() {
                    return this.warning;
                }

                public String getResetComplete() {
                    return this.resetComplete;
                }
            }

            public static class SubCmdShow
            extends AbsCommand {
                private String header = "#<rownumber> <color:#66ebff><name></color:#66ebff>:<color:#66ffb0><value>";
                private String footer = "   <click:run_command:\"<command>\"><color:#66ebff>[Add]";
                private String item = "<hover:show_text:\"<color:#ff6666>Remove\"><click:run_command:\"<commandremove>\"><color:#ff6666>X</color:#ff6666></click></hover> <hover:show_text:\"<desc>\"><color:#66ebff><shortdesc></color:#66ebff></hover>:<color:#66ffb0><state>";
                private String selectorNotFound = "<red>No selector available with that name</red>";

                public SubCmdShow() {
                    super("show", new String[0]);
                }

                public String getHeader() {
                    return this.header;
                }

                public String getFooter() {
                    return this.footer;
                }

                public String getItem() {
                    return this.item;
                }

                public String getSelectorNotFound() {
                    return this.selectorNotFound;
                }
            }

            public static class SubCmdShowOverride
            extends AbsCommand {
                private String header = "#<rownumber> <color:#66ebff><name></color:#66ebff>:<color:#66ffb0><value>";
                private String footer = "";
                private String item = "<hover:show_text:\"<desc>\"><color:#66ebff><shortdesc></color:#66ebff></hover>:<color:#66ffb0><state>";
                private String selectorNotFound = "<red>No override selector available with that name</red>";

                public SubCmdShowOverride() {
                    super("showoverride", new String[0]);
                }

                public String getHeader() {
                    return this.header;
                }

                public String getFooter() {
                    return this.footer;
                }

                public String getItem() {
                    return this.item;
                }

                public String getSelectorNotFound() {
                    return this.selectorNotFound;
                }
            }
        }
    }

    public static class Permissions {
        private Selectors selectors = new Selectors();

        public Selectors selectors() {
            return this.selectors;
        }

        public static class Selectors {
            private All all = new All();
            private Faction faction = new Faction();
            private Player player = new Player();
            private RelationAtLeast relationAtLeast = new RelationAtLeast();
            private RelationAtMost relationAtMost = new RelationAtMost();
            private RelationSingle relationSingle = new RelationSingle();
            private RoleAtLeast roleAtLeast = new RoleAtLeast();
            private RoleAtMost roleAtMost = new RoleAtMost();
            private RoleSingle roleSingle = new RoleSingle();
            private Unknown unknown = new Unknown();

            public All all() {
                return this.all;
            }

            public Faction faction() {
                return this.faction;
            }

            public Player player() {
                return this.player;
            }

            public RelationAtLeast relationAtLeast() {
                return this.relationAtLeast;
            }

            public RelationAtMost relationAtMost() {
                return this.relationAtMost;
            }

            public RelationSingle relationSingle() {
                return this.relationSingle;
            }

            public RoleAtLeast roleAtLeast() {
                return this.roleAtLeast;
            }

            public RoleAtMost roleAtMost() {
                return this.roleAtMost;
            }

            public RoleSingle roleSingle() {
                return this.roleSingle;
            }

            public Unknown unknown() {
                return this.unknown;
            }

            public static class All
            extends BasicSelector {
                private String displayValue = "Yes";

                public All() {
                    super("Match all");
                }

                public String getDisplayValue() {
                    return this.displayValue;
                }
            }

            public static class Faction
            extends BasicSelector {
                private String disbandedValue = "Disbanded! (<lastknown>)";
                private String instructions = "Provide a faction tag, like faction:CoolKids";

                protected Faction() {
                    super("Faction");
                }

                public String getDisbandedValue() {
                    return this.disbandedValue;
                }

                public String getInstructions() {
                    return this.instructions;
                }
            }

            public static class Player
            extends BasicSelector {
                @Comment(value="Used if the player is not in the Factions database (pruned?)")
                private String uuidValue = "<uuid>";
                private String instructions = "Provide a player name or UUID, like player:Trent";

                protected Player() {
                    super("Player");
                }

                public String getInstructions() {
                    return this.instructions;
                }

                public String getUuidValue() {
                    return this.uuidValue;
                }
            }

            public static class RelationAtLeast
            extends BasicSelector {
                protected RelationAtLeast() {
                    super("Relation at least");
                }
            }

            public static class RelationAtMost
            extends BasicSelector {
                protected RelationAtMost() {
                    super("Relation at most");
                }
            }

            public static class RelationSingle
            extends BasicSelector {
                protected RelationSingle() {
                    super("Relation");
                }
            }

            public static class RoleAtLeast
            extends BasicSelector {
                protected RoleAtLeast() {
                    super("Role at least");
                }
            }

            public static class RoleAtMost
            extends BasicSelector {
                protected RoleAtMost() {
                    super("Role at Most");
                }
            }

            public static class RoleSingle
            extends BasicSelector {
                protected RoleSingle() {
                    super("Role");
                }
            }

            public static class Unknown
            extends BasicSelector {
                protected Unknown() {
                    super("Unknown");
                }
            }

            public static class BasicSelector {
                private String displayName;

                protected BasicSelector(String string) {
                    this.displayName = string;
                }

                public String getDisplayName() {
                    return this.displayName;
                }
            }
        }
    }

    public static class AbsCommand {
        private List<String> aliases = new ArrayList<String>();

        protected AbsCommand(String string, String ... stringArray) {
            this.aliases.add(string);
            this.aliases.addAll(Arrays.asList(stringArray));
        }

        public List<String> getAliases() {
            return this.aliases;
        }
    }
}

