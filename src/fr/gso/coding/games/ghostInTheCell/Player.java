package fr.gso.coding.games.ghostInTheCell;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement. Ligue de bronze 880
 **/
class Player {

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        int factoryCount = in.nextInt(); // the number of factories
        Map<Integer, Factory> map = new HashMap<Integer, Factory>(factoryCount);
        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();

            if (map.get(factory1) == null) {
                map.put(factory1, new Factory(factory1));
            }
            if (map.get(factory2) == null) {
                map.put(factory2, new Factory(factory2));
            }
            // adding neighbours with their distances
            map.get(factory1).distances.put(map.get(factory2), distance);
            map.get(factory2).distances.put(map.get(factory1), distance);

        }

        // game loop
        while (true) {
            int entityCount = in.nextInt(); // the number of entities (e.g.
                                            // factories and troops)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();

                if (entityType.equals("FACTORY")) {
                    map = updateMapFactory(map, entityId, arg1, arg2, arg3);
                } else if (entityType.equals("TROOPS")) {
                    map = updateMapTroops(map, arg1, arg2, arg3, arg4, arg5);
                } else if (entityType.equals("BOMB")) {
                    // TODO voir ce que j'en fait
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            // Any valid action, such as "WAIT" or
            // "MOVE source destination cyborgs"
            System.out.println(chooseAction(map));
        }
    }

    private static String chooseAction(Map<Integer, Factory> map) {

        int nbBomb = 2;
        // regle 1 : si j'ai une usine qui contient des cyborgs mais qui ne
        // produit pas,
        // j'envoi la totalité -1 sur l'usine la plus proche ou la plus faible
        // qui produit le plus
        // et qui n'est pas à moi.
        List<String> lstActions = new ArrayList<String>();

        // Premier essai : si j'ai beaucoup de Cyborg sur une usine qui ne
        // produit pas
        // Je les déplace dans une usine proche qui produit,
        // sinon j'attaque une usine adverse faible et proche
        Factory f1 = findNonProductive(map);
        // System.err.println("NonProductive:" + f1);
        if (f1 != null) {
            // System.err.println("NonProductiveSource:" + f1);
            Factory fCible = findBestProductiveNeutralTarget(map, f1);
            if (fCible != null) {
                // System.err.println("CibleNeutreFIRST:" + fCible);
                return "MSG Droit vers:" + f1.id + ";MOVE " + f1.id + " " + fCible.id + " " + (f1.nbCyborg - 1);
            } else {
                fCible = findWeakestAdverseTarget(map, f1);
                if (fCible != null) {
                    // System.err.println("CibleAdverseFIRST:" + fCible);
                    return "MOVE " + f1.id + " " + fCible.id + " " + Integer.valueOf(fCible.nbCyborg + 1);
                }
            }
        } else {
            // On n'a pas trouvé d'usine non productive contenant des cyborgs.
            // On va partir de la plus grosse usine et on va attaquer une usine
            // neutre, qui produit le plus possible et la plus pres possible
            List<Factory> lstStrong = findFactoriesBestProductive(map);
            // System.err.println("Liste Strong:" + lstStrong.size());
            int dispatch = lstStrong.size();
            // System.err.println();
            for (Factory f : lstStrong) {
                Factory fCible = findBestProductiveNeutralTarget(map, f);
                if (fCible != null) {
                    System.err.println("CibleNeutre:" + fCible);
                    // TODO : calculer le nombre a envoyer en fonction des
                    // troupes adverses en train d'arriver
                    // pour ne pas se retrouver à poil.
                    lstActions.add("MOVE " + f.id + " " + fCible.id + " " + (f.nbCyborg - f.incomingBadCyborg));
                    // Retirer du potentiel la valeur qu'on va envoyer
                    // f.nbCyborg -= 5;
                } else {
                    System.err.println("PasDeCibleNeutre:" + fCible);
                    // TODO transformer pour retourner la cible la plus faible
                    // et la plus proche
                    fCible = findWeakestAdverseTarget(map, f);

                    if (fCible != null) {
                        // System.err.println("CibleAdverse:" + fCible);
                        lstActions.add("MOVE " + f.id + " " + fCible.id + " " + (f.nbCyborg - f.incomingBadCyborg));
                        if (nbBomb > 0) {
                            lstActions.add("BOMB " + f.id + " " + fCible.id);
                            nbBomb--;
                        }
                    }
                }
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

    private static Factory findNonProductive(Map<Integer, Factory> map) {
        List<Factory> list = new ArrayList<Factory>();
        for (Factory f : map.values()) {
            if (f.isMine != null && f.isMine && f.production == 0) {
                list.add(f);
            }
        }
        // On trouve celle qui a le plus de cyborg
        if (!list.isEmpty()) {
            list.sort(new FactoryCyborgComparator());
            Factory result = list.get(0);
            return result.nbCyborg > 0 ? result : null;
        }
        return null;

    }

    private static List<Factory> findFactoriesBestProductive(Map<Integer, Factory> map) {
        List<Factory> result = new ArrayList<Factory>();
        for (Factory f : map.values()) {
            if (f.isMine != null && f.isMine) {
                result.add(f);
            }
        }
        // Avoir les plus productives au debut
        result.sort(new FactoryProductionComparator());
        Collections.reverse(result);

        // TODO utiliser les streams Java 8
        // List<Factory> neutrals =
        // factories.values().stream()
        // .filter(Factory::isNeutral)
        // .filter(Factory::isActive)
        // .sorted(Comparator.comparing(Factory::getProduction).reversed())
        // .collect(Collectors.toList());
        // launchNeutralFight(neutrals);

        return result;
    }

    /**
     * Retour l'id d'une usine neutre, qui produit le plus possible et qui est
     * attaquable en une fois et pas trop loin
     **/
    private static Factory findBestProductiveNeutralTarget(Map<Integer, Factory> map, Factory factorySource) {
        // TODO : si plusieurs résultat, prendre la plus proche
        Map<Integer, List<Factory>> mapDistances = new HashMap<Integer, List<Factory>>();

        // System.err.println("Source:" + factorySource);

        for (Factory f : map.values()) {
            int potentialNbCyborg = f.nbCyborg;// +f.incomingBadCyborg-f.incomingGoodCyborg;
            if (f.isMine == null && potentialNbCyborg < factorySource.nbCyborg && f.production > 0) {
                // System.err.println("Eligible:" + f);
                int distance = f.distances.get(factorySource);
                if (mapDistances.get(distance) == null) {
                    mapDistances.put(distance, new ArrayList<Factory>());
                }
                mapDistances.get(distance).add(f);
            }
        }
        // Extraction de la cible la plus interessante : la plus forte parmi les
        // plus près
        for (int i = 1; i <= 20; i++) {
            // System.err.println("Distance recherche:" + i);
            List<Factory> lstTemp = mapDistances.get(i);
            if (lstTemp != null && !lstTemp.isEmpty()) {
                // Tri en prenant le nombre la production la plus forte
                lstTemp.sort(new FactoryProductionComparator());
                Collections.reverse(lstTemp);
                // System.err.println("Usine neutre attaquée :" +
                // lstTemp.get(0));
                return lstTemp.get(0);
            }
        }
        return null;
    }

    /**
     * Retourne l'id d'une usine adverse, qui produit le plus possible est qui
     * est attaquable en une fois
     **/
    private static Factory findWeakestAdverseTarget(Map<Integer, Factory> map, Factory factorySource) {
        Map<Integer, List<Factory>> mapDistances = new HashMap<Integer, List<Factory>>();
        // TODO : si plusieurs résultat, prendre la plus proche
        Factory result = null;
        for (Factory f : map.values()) {
            if (f.isMine != null && !f.isMine && f.production > 0 && f.nbCyborg < factorySource.nbCyborg) {
                // System.err.println("Eligible:" + f);
                int distance = f.distances.get(factorySource);
                if (mapDistances.get(distance) == null) {
                    mapDistances.put(distance, new ArrayList<Factory>());
                }
                mapDistances.get(distance).add(f);
            }
        }
        // Extraction de la cible la plus interessante
        for (int i = 1; i <= 20; i++) {
            // System.err.println("Distance recherche:" + i);
            List<Factory> lstTemp = mapDistances.get(i);
            if (lstTemp != null && !lstTemp.isEmpty()) {
                // Tri en prenant le nombre de cyborg le plus faible
                lstTemp.sort(new FactoryCyborgComparator());
                // System.err.println("Usine adverse attaquée :" +
                // lstTemp.get(0));
                return lstTemp.get(0);
            }
        }
        // System.err.println("MSG " + "Usines neutres:" + lstResult.size());
        return null;
    }

    private static Map<Integer, Factory> updateMapFactory(Map<Integer, Factory> map, int idFactory, int owner, int nbCyborg, int production) {
        Factory f = map.get(idFactory);
        if (owner == 1)
            f.isMine = true;
        if (owner == -1)
            f.isMine = false;
        if (owner == 0)
            f.isMine = null;
        f.nbCyborg = nbCyborg;
        f.production = production;
        // System.err.println("Maj Factory:" + f);
        return map;
    }

    private static Map<Integer, Factory> updateMapTroops(Map<Integer, Factory> map, int owner, int idFactoryDep, int idFactoryArr,
        int nbCyborg, int timeToArrival) {
        // TODO mettre à jour les cyborgs
        boolean myCyborgs = false;
        if (owner == 1)
            myCyborgs = true;
        Factory fa = map.get(idFactoryArr);
        if (myCyborgs) {
            fa.incomingGoodCyborg = nbCyborg;
        } else {
            // System.err.println("Maj Troop incBad:" + nbCyborg + fa);
            fa.incomingBadCyborg = nbCyborg;
        }
        Factory fd = map.get(idFactoryDep);

        // System.err.println("Maj Troop arr:" + fa);
        return map;
    }
}

class Factory {
    int id;

    Integer production;

    Boolean isMine = null;

    Integer nbCyborg;

    int score;

    int incomingGoodCyborg;

    int incomingBadCyborg;

    Map<Factory, Integer> distances = new HashMap<Factory, Integer>();

    public Factory(int id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id).append(" prd:").append(this.production).append(" mine:").append(this.isMine);
        sb.append(" nbCy:").append(this.nbCyborg).append(" incBad:").append(incomingBadCyborg).append(" incGood:").append(
            incomingGoodCyborg);
        sb.append(" ---> ");
        for (Factory n : this.distances.keySet()) {
            sb.append("    ").append(n.id).append(" dist:").append(this.distances.get(n));
        }

        return sb.toString();
    }
}

class FactoryProductionComparator implements Comparator<Factory> {
    public int compare(Factory f1, Factory f2) {
        return f1.production - f2.production;
    }
}

class FactoryCyborgComparator implements Comparator<Factory> {
    public int compare(Factory f1, Factory f2) {
        return f1.nbCyborg - f2.nbCyborg;
    }
}
/**
 * class FactoryProductionCyborgComparator implements Comparator<Factory> {
 * public int compare(Factory f1, Factory f2) { int c =
 * f1.production-f2.production; if (c == 0) { c = f1.cyborgs-f2.cyborgs; }
 * return c; } }
 **/

