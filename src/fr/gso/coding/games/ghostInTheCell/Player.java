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
    
    private static int NB_FACTORY = 0;

    private static final int AMOUNT_INCREMENT = 10;

    private static final int DIST_MINI_ATTACK = 8;

    private static final int NB_INCREMENT_MAX = 5;

    private static final String MOVE = "MOVE ";
    
    private static final String WAIT = "WAIT";
    

    private static final Map<Integer, Factory> MAP = new HashMap<>();

    private static final Map<Factory, Map<Integer, List<Factory>>> DISTANCES = new HashMap<>();

    private static int NB_INCREMENT = 0;

    private static int ID_FIRST_BOMB = -1;

    private static int TOUR = 0;

    private static int REMAININGBOMB = 2;

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        NB_FACTORY = in.nextInt(); // the number of factories

        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int idf1 = in.nextInt();
            int idf2 = in.nextInt();
            int distance = in.nextInt();

            alimenteMaps(idf1, idf2, distance);

        }

        gameLoop(in);
    }
    
    private static String calculStrategy() {
        
        log("-----Nouveau tour-------- " + ++TOUR);
        if (TOUR == 1) {
            return strategyMoveEnMasse();
        }
        if (NB_FACTORY > 10) {
            return strategyNombreuse();
        }
        
        return strategyDefault();
        
    }
    
    private static String strategyMoveEnMasse() {
         // Recherche d'une source non productive pour déplacment en masse
        String moveMasse = tryMoveEnMasse();
        if (moveMasse != null) {
            return moveMasse;
        }
        return null;
    }
    
    private static String strategyNombreuse() {
        log("strategyNombreuse");
        
         String moveMasse = tryMoveEnMasse();
        if (moveMasse != null) {
            return moveMasse;
        }
        
        List<String> lstActions = new ArrayList<>();
        
        List<Factory> lstStrong = MAP.values().stream().filter(o -> o.owner == 1)
        // .filter(p -> p.production > 0)
        // .sorted(Comparator.comparing(Factory::getCyborgs).reversed())
            .collect(Collectors.toList());

        for (Factory fSource : lstStrong) {

            log(fSource.toString());
            
            String attackNeutral = attackNeutral(fSource, 3);
            if (attackNeutral != null) {
                lstActions.add(attackNeutral);
                // continue;
                // return getActions(lstActions);
            }

            String attackEnemy = attackEnemy(fSource);
            if (attackEnemy != null) {
                lstActions.add(attackEnemy);
                // continue;
                // return getActions(lstActions);
            }


            
            String reinforce = reinforce(fSource);
            if (reinforce != null) {
                //lstActions.add(reinforce);
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
        
    

    private static String strategyDefault() {


        List<String> lstActions = new ArrayList<>();
       
        // J'incremente une usine à moi qui contient suffisamment de cyborg
        String incremente = tryIncremente();
        if (incremente != null) {
            //lstActions.add(incremente);
            // return incremente;
        }

        // On va partir de la plus grosse usine et on va attaquer une usine
        // neutre, qui produit le plus possible et la plus pres possible
        List<Factory> lstStrong = MAP.values().stream().filter(o -> o.owner == 1)
        // .filter(p -> p.production > 0)
        // .sorted(Comparator.comparing(Factory::getCyborgs).reversed())
            .collect(Collectors.toList());

        for (Factory fSource : lstStrong) {

            log(fSource.toString());
            
            String attackNeutral = attackNeutral(fSource);
            if (attackNeutral != null) {
                lstActions.add(attackNeutral);
                // continue;
                // return getActions(lstActions);
            }

            String attackEnemy = attackEnemy(fSource);
            if (attackEnemy != null) {
                lstActions.add(attackEnemy);
                // continue;
                // return getActions(lstActions);
            }


            String bombing = tryToBomb(fSource);
            if (bombing != null) {
                lstActions.add(bombing);
                // continue;
                // return getActions(lstActions);
            }
            
            String reinforce = reinforce(fSource);
            if (reinforce != null) {
                //lstActions.add(reinforce);
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
            
        List<Factory> factoryListSource = MAP.values().stream()
            .filter(o -> o.owner == 1)
            .collect(Collectors.toList());
            
        Factory fCible = MAP.get(0);
            
        if (factoryListSource != null 
            && !factoryListSource.isEmpty() 
            && calcDistance(factoryListSource.get(0), fCible) >= DIST_MINI_ATTACK 
            && NB_FACTORY > 10) {
            //La factory n'est pas trop près du zéro, tentative vers 0
            Factory fSource = factoryListSource.get(0);
            return MOVE + fSource.id + " " + fCible.id  + " " + fSource.nbCyborg;
        }

        factoryListSource = MAP.values().stream()
            .filter(o -> o.owner == 1)
            .filter(p -> p.production == 0)
            .collect(Collectors.toList());
            
        String result = null;
        //Si la factory qui contient le plus de cyborg ne produit pas, on incrémente et on migre ailleurs
        if (factoryListSource != null && !factoryListSource.isEmpty()) {
            Factory fSource = factoryListSource.get(0);
            result ="INC " + fSource.id;
        
            // La cible est une cible neutre et proche, on va tenter avec une grosse production
            List<Factory> factoryListCible =MAP.values().stream()
                .filter(o -> o.owner == 0)
                .filter(p -> p.production >= 2)
                //.filter(c -> c.nbCyborg == 0)
                .sorted(Comparator.comparing(Factory::getCyborgs).reversed())
                .collect(Collectors.toList());
            
            log("dfdfd" + factoryListCible.size());
            fCible = pickClosest(fSource, factoryListCible);
        
            if (fCible != null && calcDistance(fSource, fCible) <= DIST_MINI_ATTACK) {
                String move = MOVE + fSource.id + " " + fCible.id + " " + (fCible.nbCyborg+1);
                if (result != null) {
                 result = result + ";" + move;
                  return result;
                }
            return move;
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
        
       return attackNeutral(fSource, -1);
    }
    
    private static String attackNeutral(Factory fSource, int max) {
        
        List<Factory> listCible = MAP.values().stream()
            .filter(o -> o.owner == 0)
            .filter(p -> p.production > 0)
            .sorted(Comparator.comparing(Factory::getCyborgs))
            .collect(Collectors.toList());
        
        listCible = pickListInRange(fSource, listCible);

        List<String> lstActions = new ArrayList<>();

        if (listCible != null && !listCible.isEmpty()) {
            int maxAttack = max;
            if (maxAttack == -1) {
                maxAttack = fSource.nbCyborg < listCible.size() ? fSource.nbCyborg : listCible.size();
            }
            
            for (int i = 0 ; i < maxAttack ; i++) {
                Factory fCible = listCible.get(i);
                
                log("Neutral:"+fCible);
                int nb = 1;
                logAttack("AttackNeutral:", fSource, fCible, nb);
                fSource.nbCyborg -= nb;
                fSource.potentialCyborg -= nb;
                fCible.nbCyborg += nb;
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
                //logAttack("AttackEnemy:", fSource, fCible, nb);
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
            String strategy = calculStrategy();
            if (strategy != null) {
                System.out.println(strategy);
            } else {
                System.out.println(WAIT);
            }
        }
    }

    private static void displayAllFactories(String msg) {
        log(msg);
        for (Factory f : MAP.values()) {
            log(f.toString());
        }
    }

    private static void alimenteMaps(int idf1, int idf2, int distance) {

        // Effectue le get en s'assurant de la création si absent
        Factory f1 = MAP.computeIfAbsent(idf1, k -> new Factory(idf1));
        Factory f2 = MAP.computeIfAbsent(idf2, k -> new Factory(idf2));

        // Assure les inits en même temps que l'alimentation...
        DISTANCES.computeIfAbsent(f1, k -> new HashMap<>()).computeIfAbsent(distance, k -> new ArrayList<>(NB_FACTORY)).add(f2);
        DISTANCES.computeIfAbsent(f2, k -> new HashMap<>()).computeIfAbsent(distance, k -> new ArrayList<>(NB_FACTORY)).add(f1);
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
