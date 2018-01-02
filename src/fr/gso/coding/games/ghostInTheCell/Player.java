package fr.gso.coding.games.ghostInTheCell;

import java.util.*;
import java.io.*;
import java.math.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 * Refactoring Java 8
 **/
class Player {

    public static final boolean LOG = true;
    public static final int margin = 5;
    public static int idTargetMyFirstbomb = -1;

    public static Map<Integer, List<Factory>> distances = new HashMap<Integer, List<Factory>>();

    public static void log(String str) {
        if (LOG) {
            System.err.println(str);
        }
    }

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
            //TODO faire un tableau séparé qui contient les distances : ça ne change jamais !
            if (distances.get(distance) == null) {
                distances.put(distance, new ArrayList<Factory>());
            }
            distances.get(distance).add(map.get(factory2));

            if (distances.get(distance) == null) {
                distances.put(distance, new ArrayList<Factory>());
            }
            distances.get(distance).add(map.get(factory1));


        }
        // game loop
        while (true) {
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
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
                    if (arg1 == 1) {
                        idTargetMyFirstbomb = arg3;
                    }
                }
            }

            System.out.println(chooseAction(map));
        }
    }

    private static String chooseAction(Map<Integer, Factory> map) {
        log("-----Nouveau tour--------");
        int remaningBomb = 2;

        //TODO : faire un double classement
        //Comparator<Person> comparator = comparingInt(Person::getAge).thenComparing(Person::getName);

        //J'incremente une usine à moi qui contient suffisamment de cyborg
        List<Factory> listToIncrement = map.values().stream()
                .filter(o -> o.owner == 1)
                .filter(f -> f.nbCyborg >= 10)
                .filter(p -> p.production < 3)
                .sorted(Comparator.comparing(Factory::getScore))
                .collect(Collectors.toList());
        if (listToIncrement != null && !listToIncrement.isEmpty()) {
            log("*****INC******" + listToIncrement.get(0));
            listToIncrement.get(0).production++;
            return "INC " + listToIncrement.get(0).id;
        }

        List<Factory> factoryList = map.values().stream()
                .filter(o -> o.owner == 1)
                .filter(p -> p.production == 0)
                .filter(c -> c.nbCyborg > 10)
                .sorted(Comparator.comparing(Factory::getScore).reversed())
                .collect(Collectors.toList());
        if (factoryList != null && !factoryList.isEmpty()) {
            Factory fSource = factoryList.get(0);
            log("NonProductiveSource:" + fSource);

            //La cible est une cible neutre et proche, on va tenter avec une grosse production
            List<Factory> factoryListCible = map.values().stream()
                    .filter(o -> o.owner == 0)
                    .filter(p -> p.production > 0)
                    .collect(Collectors.toList());
            Factory fCible = pickClosest(fSource, factoryListCible);
            if (fCible != null) {
                return "MSG transfert vers:" + fCible.id + ";MOVE " + fSource.id + " " + fCible.id + " " + (fSource.nbCyborg - 1);
            } else {
                List<Factory> factoryListAdverse = map.values().stream()
                        .filter(o -> o.owner == -1)
                        .filter(p -> p.production > 1)
                        .filter(c -> c.nbCyborg < fSource.nbCyborg)
                        .collect(Collectors.toList());
                fCible = pickClosest(fSource, factoryListAdverse);
                if (fCible != null) {
                    return "MSG transfert vers:" + factoryListAdverse.get(0).id + ";MOVE " + fSource.id + " " + factoryListAdverse.get(0).id + " " + (fSource.nbCyborg - 1);
                }
            }
        }

        //On va tenter une action par usine de la liste
        List<String> lstActions = new ArrayList<String>();
        //On va partir de la plus grosse usine et on va attaquer une usine
        //neutre, qui produit le plus possible et la plus pres possible
        List<Factory> lstStrong = map.values().stream()
                .filter(o -> o.owner == 1)
                .filter(p -> p.production > 0)
                .sorted(Comparator.comparing(Factory::getScore).reversed())
                .collect(Collectors.toList());

        for (Factory fSource : lstStrong) {
            log("Pour la factory:" + fSource);

            List<Factory> listCible = map.values().stream()
                    .filter(o -> o.owner != 1)
                    .filter(c -> c.nbCyborg < fSource.nbCyborg)
                    .sorted(Comparator.comparing(Factory::getScore).reversed())
                    .collect(Collectors.toList());

            Factory fCible = pickClosest(fSource, listCible);
            if (fCible != null) {
                int nb = (fSource.nbCyborg > margin ? fCible.nbCyborg + 1 : fSource.production);
                lstActions.add("MSG Attaque cible:" + fCible.id + ";MOVE " + fSource.id + " " + fCible.id + " " + nb);
            }
            List<Factory> listToBomb = map.values().stream()
                    .filter(o -> o.owner == -1)
                    .filter(c -> c.nbCyborg < fSource.nbCyborg)
                    .filter(p -> p.production > 1)
                    .filter(i -> i.id != idTargetMyFirstbomb)
                    .sorted(Comparator.comparing(Factory::getScore).reversed())
                    .collect(Collectors.toList());

            Factory fToBomb = pickClosest(fSource, listToBomb);
            if (fToBomb != null && remaningBomb > 0) {
                int nb = fToBomb.nbCyborg + 1;
                lstActions.add("BOMB " + fSource.id + " " + fToBomb.id);
                lstActions.add("MOVE " + fSource.id + " " + fToBomb.id + " " + nb);
                idTargetMyFirstbomb = fToBomb.id;
                remaningBomb--;
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
        //TODO si j'ai d'autres à faire (et pour viser le ko) attaquer des bases de l'adversaire qui ne produisent pas
        return ("WAIT");
    }

    private static Factory pickClosest(Factory fSource, List<Factory> lstFactory) {
        if (lstFactory == null) {
            return null;
        }
        for (int i = 0; i <= 20; i++) {
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

    private static Map<Integer, Factory> updateMapFactory(Map<Integer, Factory> map, int idFactory, int owner, int nbCyborg, int production) {
        Factory f = map.get(idFactory);
        f.owner = owner;
        f.nbCyborg = nbCyborg;
        f.production = production;
        f.score = nbCyborg * production;
        return map;
    }

    private static Map<Integer, Factory> updateMapTroops(Map<Integer, Factory> map, int owner, int idFactoryDep, int idFactoryArr, int nbIncomingCyborg, int timeToArrival) {

        Factory fArr = map.get(idFactoryArr);

        if (owner == fArr.owner) {
            //Des cyborgs appartenant à l'usine sont en route
            if (fArr.owner == 1) {
                //Des cyborgs à moi arrivent, mon score de valeur augmente
                fArr.score += nbIncomingCyborg / timeToArrival;
            } else {
                //Des cyborgs adverses arrivent, mon score diminue
                fArr.score -= nbIncomingCyborg / timeToArrival;
            }
        } else {
            //Des cyborgs n'appartenant pas à l'usine sont en route
            if (fArr.owner == 1) {
                //Des cyborgs à moi arrivent, ça baisse le potentiel de l'usine adverse
                fArr.score -= nbIncomingCyborg / timeToArrival;
            } else {
                //Des cyborgs adverses arrivent dans l'usine adverse, ça augmente sont potentiel
                fArr.score += nbIncomingCyborg / timeToArrival;
            }

        }

        return map;
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
        sb.append(this.id)
                .append(" prd:").append(this.production)
                .append(" nbCy:").append(this.nbCyborg);

        return sb.toString();
    }
}
