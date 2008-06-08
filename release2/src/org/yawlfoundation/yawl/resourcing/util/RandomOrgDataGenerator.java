package org.yawlfoundation.yawl.resourcing.util;

import org.yawlfoundation.yawl.resourcing.ResourceManager;
import org.yawlfoundation.yawl.resourcing.resource.*;
import org.apache.log4j.Logger;

import java.util.Random;
import java.util.ArrayList;

/**
 * Author: Michael Adams
 * Creation Date: 7/02/2008
 */
public class RandomOrgDataGenerator {

    private Logger _log = Logger.getLogger(RandomOrgDataGenerator.class);
    private ResourceManager _rm;

    private String[] f = {"Alex", "Bill", "Carol", "Diane", "Errol", "Frank", "George",
                          "Hillary", "Irene", "Jack", "Kate", "Larry", "Michael",
                          "Neil", "Oprah", "Penny", "Quebert", "Ronald", "Sven",
                          "Thomas", "Uri", "Victoria", "Wendy", "Xavier", "Yvonne", "Zac"};

    private String[] l = {"Adams", "Brown", "Cooper", "Donald", "Evans", "Ford", "Green",
                          "Humphries", "Isaacs", "Jones", "Klein", "Locke", "McKay",
                          "Nelson", "O'Brien", "Petersen", "Quincy", "Roberts", "Smith",
                          "Thomson", "Ulrich", "Van Dyke", "Wilson", "Xerxes",
                          "Young", "Zappa"};

    public RandomOrgDataGenerator() {}

    public void generate(int count) {
        int HOW_MANY_PARTICIPANTS_TO_CREATE = Math.min(count, 100);

        _rm = ResourceManager.getInstance();

        // ensure it is valid to generate dummy data to org tables
        if (okToGenerate()) {
            try {
                Random rand = new Random();

                // make some roles
                Role r4 = new Role("senior analyst");
                r4.setPersisting(true);
                Role r3 = new Role("analyst");
                r3.setPersisting(true);
                Role r2 = new Role("programmer");
                r2.setPersisting(true);
                Role r1 = new Role("apprentice programmer");
                r1.setPersisting(true);

                _rm.addRole(r1);
                _rm.addRole(r2);
                _rm.addRole(r3);
                _rm.addRole(r4);

                r1.setOwnerRole(r2);
                r2.setOwnerRole(r3);
                r3.setOwnerRole(r4);

                Role[] roles = {r1, r2, r3, r4};

                // make some org groups

                OrgGroup o1 = new OrgGroup("SoftwareDiv", OrgGroup.GroupType.DIVISION, null,
                        "Software Division", true);
                _rm.addOrgGroup(o1);

                OrgGroup o2 = new OrgGroup("progDept", OrgGroup.GroupType.DEPARTMENT, o1,
                        "Programming Department", true);
                _rm.addOrgGroup(o2);

                OrgGroup o3 = new OrgGroup("teamA", OrgGroup.GroupType.TEAM, o2,
                        "Code Team A", true);
                OrgGroup o4 = new OrgGroup("teamB", OrgGroup.GroupType.TEAM, o2,
                        "Code Team B", true);
                _rm.addOrgGroup(o3);
                _rm.addOrgGroup(o4);

                // and some Positions
                Position p1 = new Position("Vice President");
                p1.setPersisting(true);
                Position p2 = new Position("manager");
                p2.setPersisting(true);
                Position p3 = new Position("supervisor");
                p3.setPersisting(true);
                Position p4 = new Position("level A");
                p4.setPersisting(true);

                _rm.addPosition(p1);
                _rm.addPosition(p2);
                _rm.addPosition(p3);
                _rm.addPosition(p4);

                p4.setReportsTo(p3);
                p3.setReportsTo(p2);
                p2.setReportsTo(p1);

                // link positions to groups

                p1.setOrgGroup(o1);
                p2.setOrgGroup(o2);
                p3.setOrgGroup(o3) ;
                p4.setOrgGroup(o3);

                Position[] positions = {p1, p2, p3, p4};

                // and some capabiities
                Capability c1 = new Capability("firstaid", "first aid officer", true);
                Capability c2 = new Capability("java", "java programmer", true);
                Capability c3 = new Capability("forklift", "forklift license", true);
                Capability c4 = new Capability("mandarin", "mandarin speaker", true);
                _rm.addCapability(c1);
                _rm.addCapability(c2);
                _rm.addCapability(c3);
                _rm.addCapability(c4);

                Capability[] capabilities = {c1, c2, c3, c4};

                // OK - let's make some Participants
                ArrayList<String> addedUsers = new ArrayList<String>();
                boolean unique;
                String last = "", first = "", user = "";
                for (int i = 0; i < HOW_MANY_PARTICIPANTS_TO_CREATE; i++) {

                    // ensure all userids are unique
                    unique = false;
                    while (! unique) {
                        first = f[rand.nextInt(26)];
                        last = l[rand.nextInt(26)];
                        user = last + first.substring(0, 1);
                        if (! addedUsers.contains(user)) {
                            addedUsers.add(user);
                            unique = true;
                        }
                    }

                    Participant p = new Participant(last, first, user, true);
                    _rm.addParticipant(p);

                    p.setAdministrator(rand.nextBoolean());
                    p.setPassword("apple");

                    p.addPosition(positions[rand.nextInt(4)]);
                    p.addCapability(capabilities[rand.nextInt(4)]);
                    p.addRole(roles[rand.nextInt(4)]);
                    p.getUserPrivileges().allowAll();
                }

                _log.info("GENERATE ORG DATA: Successfully created " +
                        HOW_MANY_PARTICIPANTS_TO_CREATE +
                        " random Participants and associated org data");
            }
            catch (Exception e) {
                _log.error("Exception thrown generating random org data", e);
            }
        }
    }

    public boolean okToGenerate() {
        String h = "GENERATE ORG DATA ERROR: ";
        String f = ". Generation of dummy data aborted";
        if (_rm.getParticipantCount() > 0) {
            _log.error(h + "Particpant table not empty" + f);
            return false;
        }
        if (! _rm.getPersisting()) {
            _log.error(h + "Persistence must be enabled to populate tables" + f);
            return false ;
        }
        if (! _rm.isDefaultOrgDB()) {
            _log.error(h + "Cannot generate dummy data to non-YAWL-default database" + f);
            return false;
        }
        return true;
    }
}