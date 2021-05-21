package algorithms;

import arwdatastruct.Agent;
import pathfinder.Route;

import java.util.HashMap;

public class Solution {

    private HashMap<Agent, Route> routes;

    public Solution(){
        this.routes = new HashMap<>();
    }

    public HashMap<Agent, Route> getRoutes() {
        return routes;
    }

    public void setRoutes(HashMap<Agent, Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Agent agent, Route route) {
        this.routes.put(agent, route);
    }

    public Route getRoute(Agent agent){
        return routes.get(agent);
    }

}
