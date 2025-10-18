/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.scoreboard.Criteria
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Scoreboard
 *  org.bukkit.scoreboard.Team
 */
package com.massivecraft.factions.scoreboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class BufferedObjective {
    private static final int MAX_LINE_LENGTH = 48;
    private static final Pattern PATTERN = Pattern.compile("(\u00a7[0-9a-fk-r])|(.)");
    private final Scoreboard scoreboard;
    private final String baseName;
    private Objective current;
    private List<Team> currentTeams = new ArrayList<Team>();
    private String title;
    private DisplaySlot displaySlot;
    private int objPtr;
    private int teamPtr;
    private boolean requiresUpdate = false;
    private final Map<Integer, String> contents = new HashMap<Integer, String>();

    public BufferedObjective(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.baseName = this.createBaseName();
        String string = this.getNextObjectiveName();
        this.current = scoreboard.registerNewObjective(string, Criteria.DUMMY, string);
    }

    private String createBaseName() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < 14) {
            stringBuilder.append(Integer.toHexString(random.nextInt()));
        }
        return stringBuilder.substring(0, 14);
    }

    public void setTitle(String string) {
        if (this.title == null || !this.title.equals(string)) {
            this.title = string;
            this.requiresUpdate = true;
        }
    }

    public void setDisplaySlot(DisplaySlot displaySlot) {
        this.displaySlot = displaySlot;
        this.current.setDisplaySlot(displaySlot);
    }

    public void setAllLines(List<String> list) {
        if (list.size() != this.contents.size()) {
            this.contents.clear();
        }
        for (int i = 0; i < list.size(); ++i) {
            this.setLine(list.size() - i, list.get(i));
        }
    }

    public void setLine(int n, String string) {
        if (string.length() > 48) {
            string = string.substring(0, 48);
        }
        string = ChatColor.translateAlternateColorCodes((char)'&', (String)string);
        if (this.contents.get(n) == null || !this.contents.get(n).equals(string)) {
            this.contents.put(n, string);
            this.requiresUpdate = true;
        }
    }

    public void hide() {
        if (this.displaySlot != null) {
            this.scoreboard.clearSlot(this.displaySlot);
        }
    }

    public void flip() {
        if (!this.requiresUpdate) {
            return;
        }
        this.requiresUpdate = false;
        String string = this.getNextObjectiveName();
        Objective objective = this.scoreboard.registerNewObjective(string, Criteria.DUMMY, string);
        objective.setDisplayName(this.title);
        ArrayList<Team> arrayList = new ArrayList<Team>();
        for (Map.Entry<Integer, String> entry : this.contents.entrySet()) {
            if (entry.getValue().length() > 16) {
                String string2;
                Team team = this.scoreboard.registerNewTeam(this.getNextTeamName());
                arrayList.add(team);
                String string3 = null;
                String string4 = null;
                String string5 = entry.getValue();
                if (string5.length() > 16) {
                    String[] stringArray = new String[3];
                    Matcher matcher = PATTERN.matcher(string5);
                    StringBuilder stringBuilder = new StringBuilder();
                    int n = 0;
                    char c = 'r';
                    char c2 = 'r';
                    while (n < 3 && matcher.find()) {
                        String string6 = matcher.group();
                        boolean bl = false;
                        if (string6.length() == 1) {
                            stringBuilder.append(string6);
                            if (stringBuilder.length() == 16) {
                                bl = true;
                            }
                        } else {
                            char c3 = string6.charAt(1);
                            if (c3 >= 'k' && c3 <= 'r') {
                                c2 = c3;
                                if (c3 == 'r') {
                                    c = 'r';
                                }
                            } else {
                                c = c3;
                                c2 = 'r';
                            }
                            if (stringBuilder.length() < 14) {
                                stringBuilder.append(string6);
                            } else {
                                bl = true;
                            }
                        }
                        if (!bl) continue;
                        stringArray[n++] = stringBuilder.toString();
                        stringBuilder = new StringBuilder();
                        if (c != 'r') {
                            stringBuilder.append('\u00a7').append(c);
                        }
                        if (c2 == 114) continue;
                        stringBuilder.append('\u00a7').append(c2);
                    }
                    if (n < 3 && !stringBuilder.isEmpty()) {
                        stringArray[n] = stringBuilder.toString();
                    }
                    if (stringArray[2] == null) {
                        string2 = stringArray[0];
                        string4 = stringArray[1];
                    } else {
                        string3 = stringArray[0];
                        string2 = stringArray[1];
                        string4 = stringArray[2];
                    }
                } else {
                    string2 = string5;
                }
                if (string3 != null) {
                    team.setPrefix(string3);
                }
                if (string4 != null) {
                    team.setSuffix(string4);
                }
                team.addEntry(string2);
                objective.getScore(string2).setScore(entry.getKey().intValue());
                continue;
            }
            objective.getScore(entry.getValue()).setScore(entry.getKey().intValue());
        }
        if (this.displaySlot != null) {
            objective.setDisplaySlot(this.displaySlot);
        }
        this.current.unregister();
        Iterator<Map.Entry<Integer, String>> iterator = this.currentTeams.iterator();
        while (iterator.hasNext()) {
            ((Team)iterator.next()).unregister();
            iterator.remove();
        }
        this.current = objective;
        this.currentTeams = arrayList;
    }

    private String getNextObjectiveName() {
        return this.baseName + "_" + this.objPtr++ % 2;
    }

    private String getNextTeamName() {
        return this.baseName.substring(0, 10) + "_" + this.teamPtr++ % 999999;
    }
}

