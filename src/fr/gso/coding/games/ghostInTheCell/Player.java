package fr.gso.coding.games.ghostInTheCell;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    public static final boolean LOG_ENABLED = true;

    public static final int MARGIN = 10;

    private static final String MOVE = "MOVE ";

    public static final Map<Integer, Factory> MAP = new HashMap<>();

    public static final Map<Factory, Map<Integer, List<Factory>>> DISTANCES = new HashMap<>();

    public static int ID_FIRST_BOMB = -1;

    public static int TOUR = 1;

    public static int REMAININGBOMB = 2;

    public static void log(String str) {
        if (LOG_ENABLED) {
            System.err.println(str);
        }
    }

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        int factoryCount = in.nextInt(); // the number of factories

        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int idf1 = in.nextInt();
            int idf2 = in.nextInt();
            int distance = in.nextInt();

            alimenteMaps(idf1, idf2, distance, factoryCount);

        }

        boolean leaveMeAlone = true;

        // game loop
        while (leaveMeAlone) {
            int entityCount = in.nextInt();
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();

                if (entityType.equals("FACTORY")) {
                    updateMapFactory(entityId, arg1, arg2, arg3);
                } else if (entityType.equals("TROOPS")) {
                    updateMapTroops(arg1, arg2, arg3, arg4, arg5);
                } else if (entityType.equals("BOMB")) {
                    if (arg1 == 1) {
                        ID_FIRST_BOMB = arg3;
                    }
                }
            }

            System.out.println(chooseAction());
        }
    }

    private static void alimenteMaps(int idf1, int idf2, int distance, int factoryCount) {

        // Effectue le get en s'assurant de la création si absent
        Factory f1 = MAP.computeIfAbsent(idf1, k -> new Factory(idf1));
        Factory f2 = MAP.computeIfAbsent(idf2, k -> new Factory(idf2));

        // Assure les inits en même temps que l'alimentation...
        DISTANCES.computeIfAbsent(f1, k -> new HashMap<>()).computeIfAbsent(distance, k -> new ArrayList<>(factoryCount)).add(f2);
        DISTANCES.computeIfAbsent(f2, k -> new HashMap<>()).computeIfAbsent(distance, k -> new ArrayList<>(factoryCount)).add(f1);
    }

    private static String tryIncremente() {
        List<Factory> listToIncrement =
            MAP.values().stream().filter(o -> o.owner == 1).filter(f -> f.nbCyborg >= MARGIN).filter(p -> p.production < 3).sorted(
                Comparator.comparing(Factory::getScore)).collect(Collectors.toList());

        if (listToIncrement != null && !listToIncrement.isEmpty()) {
            log("*****INC******" + listToIncrement.get(0));
            listToIncrement.get(0).nbCyborg -= 10;
            return "INC " + listToIncrement.get(0).id;
        }
        return null;
    }

    private static String tryMoveEnMasse() {
        List<Factory> factoryList =
            MAP.values().stream().filter(o -> o.owner == 1).filter(p -> p.production == 0).filter(c -> c.nbCyborg > MARGIN).sorted(
                Comparator.comparing(Factory::getScore).reversed()).collect(Collectors.toList());
        if (factoryList != null && !factoryList.isEmpty()) {
            Factory fSource = factoryList.get(0);
            log("NonProductiveSource:" + fSource);

            // La cible est une cible neutre et proche, on va tenter avec une
            // grosse production
            List<Factory> factoryListCible =
                MAP.values().stream().filter(o -> o.owner == 0).filter(p -> p.production > 0).sorted(
                    Comparator.comparing(Factory::getScore).reversed()).collect(Collectors.toList());
            Factory fCible = pickClosest(fSource, factoryListCible);
            if (fCible != null) {
                fSource.nbCyborg -= fSource.nbCyborg - 1;
                return MOVE + fSource.id + " " + fCible.id + " " + (fSource.nbCyborg - 1);
            } else {
                List<Factory> factoryListAdverse =
                    MAP.values().stream().filter(o -> o.owner == -1).filter(p -> p.production > 1).filter(
                        c -> c.nbCyborg < fSource.nbCyborg).collect(Collectors.toList());
                fCible = pickClosest(fSource, factoryListAdverse);
                if (fCible != null) {
                    fSource.nbCyborg -= fSource.nbCyborg - 1;
                    return MOVE + fSource.id + " " + factoryListAdverse.get(0).id + " " + (fSource.nbCyborg - 1);
                }
            }
        }
        return null;
    }

    private static String chooseAction() {
        System.err.println("-----Nouveau tour-------- " + TOUR++);

        // TODO : faire un double classement
        // Comparator<Person> comparator =
        // comparingInt(Person::getAge).thenComparing(Person::getName);

        List<String> lstActions = new ArrayList<>();

        // J'incremente une usine à moi qui contient suffisamment de cyborg
        String incremente = tryIncremente();
        if (incremente != null) {
            lstActions.add(incremente);
        }

        // Recherche d'une source non productive pour déplacment en masse
        String moveMasse = tryMoveEnMasse();
        if (incremente == null && moveMasse != null) {
            lstActions.add(moveMasse);
        }

        // On va partir de la plus grosse usine et on va attaquer une usine
        // neutre, qui produit le plus possible et la plus pres possible
        List<Factory> lstStrong =
            MAP.values().stream().filter(o -> o.owner == 1).filter(p -> p.production > 0).sorted(
                Comparator.comparing(Factory::getScore).reversed()).collect(Collectors.toList());

        for (Factory fSource : lstStrong) {

            String attackNeutral = attackNeutral(fSource);
            if (attackNeutral != null) {
                lstActions.add(attackNeutral);
                continue;
                // return attackNeutral;
            }

            String reinforce = reinforce(fSource);
            if (reinforce != null) {
                lstActions.add(reinforce);
                continue;
                // return reinforce;
            }

            String bombing = tryToBomb(fSource);
            if (bombing != null) {
                lstActions.add(bombing);
                continue;
                // return bombing;
            }

            String attackEnemy = attackEnemy(fSource);
            if (attackEnemy != null) {
                lstActions.add(attackEnemy);
                continue;
                // return attackEnemy;
            }

        }

        if (lstActions.size() > 0) {
            StringBuilder sb = new StringBuilder();
            boolean start = true;
            for (String s : lstActions) {
                if (!start) {
                    sb.append(";");
                } else {
                    start = false;
                }
                sb.append(s);
            }
            return sb.toString();
        }
        // TODO si j'ai d'autres à faire (et pour viser le ko) attaquer des
        // bases de l'adversaire qui ne produisent pas
        return ("WAIT");
    }

    private static String attackNeutral(Factory fSource) {

        List<Factory> listCible = MAP.values().stream().filter(o -> o.owner == 0)
        // .filter(c -> c.nbCyborg < fSource.nbCyborg)
        // .sorted(Comparator.comparing(Factory::getScore).reversed())
            .collect(Collectors.toList());

        Factory fCible = pickClosest(fSource, listCible);

        if (fCible != null) {
            int nb = (fSource.nbCyborg > MARGIN ? fCible.nbCyborg + 1 : fSource.production);
            log("attack neutral");
            fSource.nbCyborg -= nb;
            fCible.nbCyborg += nb;
            return MOVE + fSource.id + " " + fCible.id + " " + nb;
        }
        return null;
    }

    private static String attackEnemy(Factory fSource) {

        List<Factory> listCible = MAP.values().stream().filter(o -> o.owner == -1)
        // .filter(c -> c.nbCyborg < fSource.nbCyborg)
        // .sorted(Comparator.comparing(Factory::getScore).reversed())
            .collect(Collectors.toList());

        Factory fCible = pickClosest(fSource, listCible);

        if (fCible != null) {
            int nb = (fSource.nbCyborg > MARGIN ? fCible.nbCyborg + 1 : fSource.production);
            log("attack enemy");
            fSource.nbCyborg -= nb;
            fCible.nbCyborg += nb;
            return MOVE + fSource.id + " " + fCible.id + " " + nb;
        }
        return null;
    }

    private static String reinforce(Factory fSource) {

        List<Factory> listCible = MAP.values().stream().filter(o -> o.owner == 1).filter(c -> c.nbCyborg < MARGIN)
        // .sorted(Comparator.comparing(Factory::getScore).reversed())
            .collect(Collectors.toList());

        Factory fCible = pickClosest(fSource, listCible);

        if (fCible != null) {
            log("reinforce");
            return MOVE + fSource.id + " " + fCible.id + " " + fSource.production;
        }
        return null;
    }

    private static String tryToBomb(Factory fSource) {
        List<Factory> listToBomb = MAP.values().stream().filter(o -> o.owner == -1)
        // .filter(c -> c.nbCyborg < fSource.nbCyborg)
        // .filter(p -> p.production > 1)
            .filter(i -> i.id != ID_FIRST_BOMB).sorted(Comparator.comparing(Factory::getScore).reversed()).collect(Collectors.toList());

        Factory fToBomb = pickClosest(fSource, listToBomb);
        if (fToBomb != null && REMAININGBOMB > 0) {
            ID_FIRST_BOMB = fToBomb.id;
            REMAININGBOMB--;
            log("bomb");
            return "BOMB " + fSource.id + " " + fToBomb.id;
        }
        return null;
    }

    private static Factory pickClosest(Factory fSource, List<Factory> lstFactory) {
        if (fSource == null || lstFactory == null) {
            return null;
        }
        Map<Integer, List<Factory>> distances = DISTANCES.get(fSource);
        // log("taille table distances pour " + fSource + " :"
        // +distances.keySet().size());
        for (int i = 0; i <= 20; i++) {
            List<Factory> lstDistances = distances.get(i);
            if (lstDistances != null && !lstDistances.isEmpty()) {
                // log("----distance de :" + i + " :" + lstDistances.size());
                for (Factory iFactory : lstDistances) {
                    // log("--------factory :" + iFactory);
                    if (lstFactory.contains(iFactory)) {
                        return iFactory;
                    }
                }
            }
        }
        return null;
    }

    private static void updateMapFactory(int idFactory, int owner, int nbCyborg, int production) {
        Factory f = MAP.get(idFactory);
        f.owner = owner;
        f.nbCyborg = nbCyborg;
        f.production = production;
        f.score = nbCyborg * (production + 1);
    }

    private static void updateMapTroops(int owner, int idFactoryDep, int idFactoryArr, int nbIncomingCyborg, int timeToArrival) {

        Factory fArr = MAP.get(idFactoryArr);
        // TODO : vérifier que les scores sont correctement mis à jour
        if (owner == fArr.owner) {
            // Des cyborgs appartenant à l'usine sont en route
            if (fArr.owner == 1) {
                // Des cyborgs à moi arrivent, mon score de valeur augmente
                fArr.score += nbIncomingCyborg / timeToArrival;
            } else {
                // Des cyborgs adverses arrivent, mon score diminue
                fArr.score -= nbIncomingCyborg / timeToArrival;
            }
        } else {
            // Des cyborgs n'appartenant pas à l'usine sont en route
            if (fArr.owner == 1) {
                // Des cyborgs à moi arrivent, ça baisse le potentiel de l'usine
                // adverse
                fArr.score -= nbIncomingCyborg / timeToArrival;
            } else {
                // Des cyborgs adverses arrivent dans l'usine adverse, ça
                // augmente sont potentiel
                fArr.score += nbIncomingCyborg / timeToArrival;
            }
        }
    }
}

class Factory {
    int id;

    Integer production;

    Integer owner;

    Integer nbCyborg;

    int score;

    public Factory(int id) {
        this.id = id;
    }

    public Integer getProduction() {
        return this.production;
    }

    public Integer getCyborgs() {
        return this.nbCyborg;
    }

    public Integer getScore() {
        return this.score;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id).append(" prd:").append(this.production).append(" nbCy:").append(this.nbCyborg).append(" score:").append(
            this.score);

        return sb.toString();
    }
}
