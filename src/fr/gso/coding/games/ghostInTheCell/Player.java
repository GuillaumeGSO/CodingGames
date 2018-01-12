package fr.gso.coding.games.ghostInTheCell;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

    private static final boolean LOG_ENABLED = true;

    private static final int MARGIN = 6;

    private static final int AMOUNT_INCREMENT = 10;

    private static final int DIST_MINI_ATTACK = 8;

    private static final int NB_INCREMENT_MAX = 5;

    private static final String MOVE = "MOVE ";

    private static final Map<Integer, Factory> MAP = new HashMap<>();

    private static final Map<Factory, Map<Integer, List<Factory>>> DISTANCES = new HashMap<>();

    private static int NB_INCREMENT = 0;

    private static int ID_FIRST_BOMB = -1;

    private static int TOUR = 0;

    private static int REMAININGBOMB = 2;

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

        gameLoop(in);
    }

    private static String chooseAction() {

        System.err.println("-----Nouveau tour-------- " + TOUR++);

        List<String> lstActions = new ArrayList<>();

        // Recherche d'une source non productive pour déplacment en masse
        String moveMasse = tryMoveEnMasse();
        if (moveMasse != null) {
            lstActions.add(moveMasse);
            // return moveMasse;
        }

        // J'incremente une usine à moi qui contient suffisamment de cyborg
        String incremente = tryIncremente();
        if (incremente != null) {
            lstActions.add(incremente);
            // return incremente;
        }

        // On va partir de la plus grosse usine et on va attaquer une usine
        // neutre, qui produit le plus possible et la plus pres possible
        List<Factory> lstStrong = MAP.values().stream().filter(o -> o.owner == 1)
        // .filter(p -> p.production > 0)
        // .sorted(Comparator.comparing(Factory::getCyborgs).reversed())
            .collect(Collectors.toList());

        for (Factory fSource : lstStrong) {

            //log(fSource.toString());
            String reinforce = reinforce(fSource);
            if (reinforce != null) {
                lstActions.add(reinforce);
                // continue;
                // return getActions(lstActions);
            }

            String attackEnemy = attackEnemy(fSource);
            if (attackEnemy != null) {
                lstActions.add(attackEnemy);
                // continue;
                // return getActions(lstActions);
            }

            String attackNeutral = attackNeutral(fSource);
            if (attackNeutral != null) {
                lstActions.add(attackNeutral);
                // continue;
                // return getActions(lstActions);
            }


            String bombing = tryToBomb(fSource);
            if (bombing != null) {
                lstActions.add(bombing);
                // continue;
                // return getActions(lstActions);
            }

        }

        return getActions(lstActions);

    }

    private static String tryIncremente() {
        List<Factory> listToIncrement =
            MAP.values().stream().filter(o -> o.owner == 1).filter(f -> f.nbCyborg >= AMOUNT_INCREMENT).filter(p -> p.production < 3)
                .collect(Collectors.toList());

        if (listToIncrement != null && !listToIncrement.isEmpty()) {
            Factory fIncr = listToIncrement.get(0);
            if (fIncr.nbCyborg - AMOUNT_INCREMENT >= fIncr.production && fIncr.potentialCyborg > 0 && NB_INCREMENT < NB_INCREMENT_MAX) {
                log("*****INC******" + fIncr);
                fIncr.nbCyborg -= AMOUNT_INCREMENT;
                fIncr.tourIncremented = TOUR;
                NB_INCREMENT++;
                return "INC " + fIncr.id;
            }
        }
        return null;
    }

    private static String tryMoveEnMasse() {
        if (TOUR == 1) {
            List<Factory> factoryList = MAP.values().stream().filter(o -> o.owner == 1).collect(Collectors.toList());
            if (factoryList != null && factoryList.size() == 1 && calcDistance(factoryList.get(0), MAP.get(0)) > DIST_MINI_ATTACK) {
                return MOVE + factoryList.get(0).id + " 0 " + factoryList.get(0).nbCyborg;
            }
        }

        if (TOUR > 5) {
            return null;
        }

        List<Factory> factoryList =
            MAP.values().stream().filter(o -> o.owner == 1).filter(p -> p.production == 0).collect(Collectors.toList());
        if (factoryList != null && factoryList.size() == 1) {
            Factory fSource = factoryList.get(0);
            log("CandidatEnMasse:" + fSource);

            // La cible est une cible neutre et proche, on va tenter avec une
            // grosse production
            List<Factory> factoryListCible =
                MAP.values().stream().filter(o -> o.owner == 0).filter(p -> p.production > 1).filter(c -> c.nbCyborg < fSource.nbCyborg)
                    .collect(Collectors.toList());

            Factory fCible = pickClosest(fSource, factoryListCible);
            log("CibleEnMasse:" + fCible);
            if (fCible != null && calcDistance(fSource, fCible) <= DIST_MINI_ATTACK) {
                return MOVE + fSource.id + " " + fCible.id + " " + fSource.nbCyborg;
            }
        }
        return null;
    }

    private static String getActions(List<String> lstActions) {
        if (lstActions != null && lstActions.size() > 0) {
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
        return "WAIT";
    }

    private static int calcAttack(Factory fSource, Factory fCible) {

        int nb = fSource.production;

        for (int i = nb; i < fSource.nbCyborg; i++) {
            if (fSource.potentialCyborg >= fSource.nbCyborg && fSource.nbCyborg - nb > MARGIN) {
                log(i + ":" + fSource.toString());
                nb = i;
            }
        }

        return nb;
    }

    private static String attackNeutral(Factory fSource) {
        List<Factory> listCible = MAP.values().stream().filter(o -> o.owner == 0).collect(Collectors.toList());
        listCible = pickListInRange(fSource, listCible);

        List<String> lstActions = new ArrayList<>();

        if (listCible != null && !listCible.isEmpty()) {

            for (Factory fCible : listCible) {
                int nb = fCible.nbCyborg + 1;
                if (fSource.nbCyborg - nb <= MARGIN && fSource.production > 1) {
                    continue;
                }
                // logAttack("AttackNeutral:", fSource, fCible, nb);
                fSource.nbCyborg -= nb;
                fSource.potentialCyborg -= nb;
                // fCible.nbCyborg += nb;
                fCible.potentialCyborg -= nb;
                lstActions.add(MOVE + fSource.id + " " + fCible.id + " " + nb);
            }
        }
        return lstActions.isEmpty() ? null : getActions(lstActions);
    }

    private static String attackEnemy(Factory fSource) {

        List<Factory> listCible = MAP.values().stream().filter(o -> o.owner == -1).collect(Collectors.toList());

        listCible = pickListInRange(fSource, listCible);

        List<String> lstActions = new ArrayList<>();

        if (listCible != null && !listCible.isEmpty()) {

            for (Factory fCible : listCible) {
                int nb = calcAttack(fSource, fCible);
                logAttack("AttackEnemy:", fSource, fCible, nb);
                fSource.nbCyborg -= nb;
                fSource.potentialCyborg -= nb;
                fCible.nbCyborg += nb;
                fCible.potentialCyborg -= nb;
                lstActions.add(MOVE + fSource.id + " " + fCible.id + " " + nb);
            }
        }
        return lstActions.isEmpty() ? null : getActions(lstActions);
    }

    private static String reinforce(Factory fSource) {
            if( fSource.id==0) {
                return null;
            }
            //ici la source va systématiquement alimenter la factory 0
            Factory fCible  = MAP.get(0);
            int nb = fSource.production;
            if (fSource.nbCyborg - nb <= MARGIN) {
                return null;
            }
            logAttack("Reinforce:", fSource, fCible, nb);
            fSource.nbCyborg -= nb;
            fCible.nbCyborg += nb;
            return MOVE + fSource.id + " " + fCible.id + " " + nb;
    }

    private static String tryToBomb(Factory fSource) {
        List<Factory> listToBomb =
            MAP.values().stream().filter(o -> o.owner == -1).filter(p -> p.production > 1).filter(i -> i.id != ID_FIRST_BOMB).collect(
                Collectors.toList());

        Factory fToBomb = pickClosest(fSource, listToBomb);
        if (fToBomb != null && REMAININGBOMB > 0 && calcDistance(fSource, fToBomb) <= DIST_MINI_ATTACK) {
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
        for (int i = 1; i < DIST_MINI_ATTACK; i++) {
            List<Factory> lstDistances = distances.get(i);
            if (lstDistances != null && !lstDistances.isEmpty()) {
                for (Factory iFactory : lstDistances) {
                    if (lstFactory.contains(iFactory)) {
                        return iFactory;
                    }
                }
            }
        }
        return null;
    }

    private static List<Factory> pickListInRange(Factory fSource, List<Factory> lstFactory) {
        if (fSource == null || lstFactory == null) {
            return null;
        }
        List<Factory> lstResult = new ArrayList<>();
        Map<Integer, List<Factory>> distances = DISTANCES.get(fSource);
        for (int i = 1; i <= DIST_MINI_ATTACK; i++) {
            List<Factory> lstDistances = distances.get(i);
            if (lstDistances != null && !lstDistances.isEmpty()) {
                for (Factory iFactory : lstDistances) {
                    if (lstFactory.contains(iFactory)) {
                        lstResult.add(iFactory);
                    }
                }
            }
        }
        return lstResult;
    }

    private static int calcDistance(Factory fSource, Factory fCible) {
        Map<Integer, List<Factory>> distances = DISTANCES.get(fSource);
        int dist = 0;
        for (int i = 1; i < 20; i++) {
            List<Factory> lstDistances = distances.get(i);
            if (lstDistances != null && !lstDistances.isEmpty()) {
                for (Factory iFactory : lstDistances) {
                    if (iFactory.id == fCible.id) {
                        return i;
                    }
                }
            }
        }
        return dist;
    }

    public static void log(String str) {
        if (LOG_ENABLED) {
            System.err.println(str);
        }
    }

    public static void logAttack(String str, Factory fSource, Factory fCible, int nb) {
        log(str + " " + fSource.id + " -> " + fCible.id + " : " + nb);
    }

    private static void gameLoop(Scanner in) {
        boolean leaveMeAlone = true;

        // game loop
        while (leaveMeAlone) {
            int entityCount = in.nextInt();
            // displayAllFactories("BeforeUpdate");
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();

                if (entityType.equals("FACTORY")) {
                    updateFactory(entityId, arg1, arg2, arg3);
                } else if (entityType.equals("TROOP")) {
                    updateTroops(arg1, arg2, arg3, arg4, arg5);
                } else if (entityType.equals("BOMB")) {
                    if (arg1 == 1) {
                        ID_FIRST_BOMB = arg3;
                    }
                }
            }

            // displayAllFactories("AfterUpdate");
            System.out.println(chooseAction());
        }
    }

    private static void displayAllFactories(String msg) {
        log(msg);
        for (Factory f : MAP.values()) {
            log(f.toString());
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

    private static void updateFactory(int idFactory, int owner, int nbCyborg, int production) {
        Factory f = MAP.get(idFactory);
        f.owner = owner;
        f.nbCyborg = nbCyborg;
        f.potentialCyborg = nbCyborg;
        f.production = production;
    }

    private static void updateTroops(int owner, int idFactoryDep, int idFactoryArr, int nbIncomingCyborg, int timeToArrival) {

        Factory fArr = MAP.get(idFactoryArr);

        if (timeToArrival <= DIST_MINI_ATTACK) {
            if (owner == fArr.owner) {
                fArr.potentialCyborg += nbIncomingCyborg;// / timeToArrival;
            } else {
                fArr.potentialCyborg -= nbIncomingCyborg;// / timeToArrival;
            }
        }
    }

}

class Factory {
    int id;

    Integer production;

    Integer owner;

    Integer nbCyborg;

    Integer potentialCyborg;

    Integer tourIncremented = 0;

    public Factory(int id) {
        this.id = id;
    }

    public Integer getProduction() {
        return this.production;
    }

    public Integer getCyborgs() {
        return this.nbCyborg;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id).append(" - owner:").append(this.owner).append(" prd:").append(this.production).append(" nbCy:").append(
            this.nbCyborg).append(" potential:").append(this.potentialCyborg);

        return sb.toString();
    }
}
