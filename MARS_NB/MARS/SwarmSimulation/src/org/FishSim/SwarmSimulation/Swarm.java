package org.FishSim.SwarmSimulation;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Mandy Feldvoß
 */

public class Swarm implements IFoodSource{
    protected List<Fish> swarm;
    protected FishSim sim;
    protected FoodSourceMap ownMap;
    protected ArrayList<FoodSourceMap> foreignMaps;
    protected Vector3f scale;
    protected float near;
    protected int id;
    protected Random random = new Random();
    protected Vector3f center;
    protected float colRadius;
    protected float viewRadius;
    protected SwarmColControl colCont;
    protected SwarmViewControl viewCont;
    private Vector3f colLocation = null;
    private Vector3f viewLocation = null;
    private Vector3f splitLocation = null;
    protected int type;
    protected float moveSpeed = 1f;
    protected float rotationSpeed = 1;
    private float avoidTime = 0f;
    private float targetTime = 0f;
    protected float splitTime = 0f;
    private float searchTime = 0f;
    protected float escapeInc = 0;
    private Vector3f lastCenter = Vector3f.ZERO;
    private boolean collided = false;
    private boolean viewCollided = false;
    protected boolean split = false;
    protected boolean merge = false;
    private Swarm mergeWith;
    private Vector3f searchVec;
    
    /**
     *
     * @param sim       Simulation
     * @param size      Size of the swarm
     * @param scale
     * @param spawn     Spawnpoint of the fish
     * @param ownMap       Foodsourcemap where the fish belongs to
     * @param type      Type of the fish
     * @param id        Id of the swarm
     */
    public Swarm(FishSim sim, int size, Vector3f scale, Vector3f spawn, FoodSourceMap ownMap, int type, int id){
        colRadius = (float) ((Math.log1p((float)size)) * scale.length());
        viewRadius = colRadius + 5;
        near = (float)  3 * scale.length();
        this.sim = sim;
        swarm = new ArrayList<Fish>();
        center = spawn;
        this.scale = scale;
        this.type = type;
        this.id = id;
        this.ownMap = ownMap;
        
        for(int i = 0; i < size; i++){
            swarm.add(new Fish(sim, scale, new Vector3f(0.0f, 0.0f, 0.0f), spawn, this));
            swarm.get(i).show();
        }
        initCollidable();
    }
    
    /**
     *
     * @param sim       Simulation
     * @param size      Size of the fish
     * @param spawn     Spawnpoint of the fish
     * @param ownMap       Foodsourcemap where the fish belongs to
     * @param type      Type of the fish
     * @param id        Id of the swarm
     */
    public Swarm(FishSim sim, int size, Vector3f scale, float deviation, Vector3f spawn, FoodSourceMap ownMap, int type, int id){
        colRadius = (float) ((Math.log1p((float)size)) * scale.length());
        viewRadius = colRadius + 5;
        near = (float)  3 * scale.length();
        this.sim = sim;
        swarm = new ArrayList<Fish>();
        center = spawn;
        this.scale = scale;
        this.type = type;
        this.id = id;
        this.ownMap = ownMap;
        
        float rand;
        for(int i = 0; i < size; i++){
            rand = getGaussianDistributionNoise(deviation);
            swarm.add(new Fish(sim, scale.add(rand, rand, rand), new Vector3f(0.0f, 0.0f, 0.0f), spawn, this));
            swarm.get(i).show();
        }
        initCollidable();
    }
    
    /**
     *
     * @param sim       Simulation
     * @param swarm     List of fishes
     * @param spawn     Spawnpoint of the fish
     * @param ownMap       Foodsourcemap where the fish belongs to
     * @param type      Type of the fish
     * @param id        Id of the swarm
     */
    public Swarm(FishSim sim, ArrayList<Fish> swarm, Vector3f scale, Vector3f spawn, FoodSourceMap ownMap, int type, int id){
        scale = new Vector3f(0.25f, 0.25f, 0.25f);
        colRadius = (float) ((Math.log1p((float)swarm.size())) * scale.length());
        viewRadius = colRadius + 5;
        near = (float)  3 * scale.length();
        this.sim = sim;
        this.swarm = swarm;
        center = spawn;
        this.scale = scale;
        this.type = type;
        this.id = id;
        this.ownMap = ownMap;
        initCollidable();
    }
       
    private void initCollidable(){
        SphereCollisionShape colSphere = new SphereCollisionShape(colRadius);
        colCont = new SwarmColControl(colSphere, this);
        colCont.setCollisionGroup(04);
        colCont.setCollideWithGroups(01);
        colCont.setCollideWithGroups(04);
        colCont.setKinematic(true);
        SphereCollisionShape viewSphere = new SphereCollisionShape(viewRadius);
        viewCont = new SwarmViewControl(viewSphere, this);
        viewCont.setCollisionGroup(05);
        viewCont.setCollideWithGroups(04);
        viewCont.setKinematic(true);
        enableCol();
    }

    /**
     *
     * @param tpf   Time per frame
     */
    public void move(float tpf) {
        //computeRadius();
        computeCenter();
        colCont.setPhysicsLocation(center);
        viewCont.setPhysicsLocation(center);
        
        if(searchTime <= 0f){
            searchTime = (float) Math.random()*10f;
            searchVec = new Vector3f((float) (Math.random()-Math.random()), (float) (Math.random()-Math.random()), (float) (Math.random()-Math.random()));
        }else{
            searchTime -= tpf;
        }
        
        if(collided){
            collided = false;
            avoidTime = 2.0f;
        }else{
            if(avoidTime <= 0f){
                colLocation = null;
            }else{
                avoidTime -= tpf;
            }
        }
        
        if(viewCollided){
            viewCollided = false;
            targetTime = 30f;
            escapeInc = 2*moveSpeed;
        }else{
            if(targetTime <= 0){
                viewLocation = null;
                escapeInc = 0;
            }else{
                targetTime -= tpf;
                escapeInc = 2*moveSpeed * targetTime/30f;
                System.out.println(escapeInc);
            }
        }
        
        if(splitTime > 0){
            splitTime -= tpf;
        }
        
        for(int i = 0; i < swarm.size(); i++){
            swarm.get(i).swim(tpf);
        }
        
        if(split){
            split();
        }else{
            if(merge){
                merge();
            }
        }
    }
    
    public void stat(){
        for(int i = 0; i < swarm.size(); i++){
            System.out.println(swarm.get(i).getTriangleCount());
        }
    }
        
    public void setMoveSpeed(float moveSpeed){
        this.moveSpeed = moveSpeed;
    }
    
    public void setRotationSpeed(float rotationSpeed){
        this.rotationSpeed = rotationSpeed;
    }
        
    /**
     *
     * @param splitLocation     Location where the swarm should split up
     */
    public void setSplit(Vector3f splitLocation){
        split = true;
        this.splitLocation = splitLocation;
    }
    
    /**
     * Split the swarm
     */
    public void split(){
        disableCol();

        ArrayList<Fish> fishList1 = new ArrayList<Fish>();
        ArrayList<Fish> fishList2 = new ArrayList<Fish>();
        for(int i = 0; i < swarm.size(); i++){
            if(swarm.get(i).getLocalTranslation().y < splitLocation.y){
                fishList1.add(swarm.get(i));
            }else{
                fishList2.add(swarm.get(i));
            }
        }
        
        Swarm split1 = new Swarm(sim, fishList1, scale, center, ownMap, type, id);
        Swarm split2 = new Swarm(sim, fishList2, scale, center, ownMap, type, id);
                
        split1.setCollided(splitLocation);
        split1.resetSplitTime();
        split2.setCollided(splitLocation);
        split2.resetSplitTime();
        
        ArrayList<Swarm> swarms = new ArrayList<Swarm>();
        swarms.add(split1);
        swarms.add(split2);
        sim.swarms.addAll(swarms);
        sim.removedSwarms.add(this);
    }
    /**
     *
     * @param mergeWith     Swarm to merge with
     */
    public void setMerge(Swarm mergeWith){
        merge = true;
        this.mergeWith = mergeWith;
    }
    
    /**
     * Merge two swarms
     */
    public void merge(){
        disableCol();
        mergeWith.disableCol();
        
        Vector3f newCenter = center.add(mergeWith.center).divide(2);
        ArrayList<Fish> newFishList = new ArrayList<Fish>();
        
        for(int i = 0; i < swarm.size(); i++){
            newFishList.add(swarm.get(i));
        }
        for(int i = 0; i < mergeWith.swarm.size(); i++){
            newFishList.add(mergeWith.swarm.get(i));
        }
        Swarm merged = new Swarm(sim, newFishList, scale.add(mergeWith.scale).divide(2f),  newCenter, ownMap, type, id);
        
        sim.swarms.add(merged);
        ArrayList<Swarm> swarms = new ArrayList<Swarm>();
        swarms.add(this);
        swarms.add(mergeWith);
        sim.removedSwarms.addAll(swarms);
    }
    
    /**
     *
     * @param fish
     */
    public void add(Fish fish){
        swarm.add(fish);
        fish.setSwarm(this);
    }
    
    /**
     *
     * @param fish
     * @return Neighbourhoods
     */
    public ArrayList<Fish> getNearNeigh(Fish fish){
        ArrayList<Fish> neigh = new ArrayList<Fish>();
        float dist;
        float angle;
        for(int i = 0; i < swarm.size(); i++){
            dist = fish.getLocalTranslation().distance(swarm.get(i).getLocalTranslation());
            if(dist <= near){
                angle = (float) Math.toDegrees(fish.lastMove.normalize().angleBetween(swarm.get(i).getLocalTranslation().subtract(fish.getLocalTranslation()).normalize()));
                if(angle < 150f){
                    neigh.add(swarm.get(i));
                }
            }
        }
        neigh.remove(fish);
        return neigh;
    }
    
    /**
     *
     * @return Center of the swarm
     */
    public Vector3f getCenter(){
        return center;
    }
    
    public Vector3f getDirection(Fish fish, float tpf){
        IFoodSource source = ownMap.getNearestFS(fish.getLocalTranslation());
        if(source == null || !fish.isHungry()){
            fish.getHungry(tpf);
            if(fish.getLocalTranslation().add(searchVec).y > sim.getIniter().getCurrentWaterHeight(fish.getLocalTranslation().x, fish.getLocalTranslation().z - colRadius)){
                searchVec.negateLocal();
            }
            return fish.getLocalTranslation().add(searchVec.normalize().mult(viewRadius+1));
        }else{
            Vector3f location = source.getNearestLocation(fish.getLocalTranslation());
            float dist = fish.getLocalTranslation().distance(location);
            if(dist <= viewRadius){
                if(dist < scale.length()){
                    fish.eat(source, tpf);
                }
                return location;
            }else{
                return location.add(searchVec.mult(dist));
            }
        }
    }
    
    
    private void computeCenter(){
        lastCenter = center;
        center = new Vector3f().zero();
        for(int i = 0; i < swarm.size(); i++){
            center.addLocal(swarm.get(i).getLocalTranslation());
        }
        center.divideLocal((float) swarm.size());
    }
    
    /**
     *
     * @return MoveDirection
     */
    public Vector3f getMoveDirection(){
        return center.subtract(lastCenter);
    }
    
    /**
     *
     * @return near
     */
    public float getNear(){
        return near;
    }
    
    /**
     *
     * @param cLocation     Location of the collision from the colControl
     */
    public void setCollided(Vector3f cLocation){
        collided = true;
        colLocation = cLocation;
    }
    
    /**
     *
     * @param vLocation     Location of the collision from the viewControl
     */
    public void setViewCollided(Vector3f vLocation){
        viewCollided = true;
        viewLocation = vLocation;
    }
    
    /**
     * Enable Collosion
     */
    public void enableCol(){
        sim.getBulletAppState().getPhysicsSpace().add(colCont);
        sim.getBulletAppState().getPhysicsSpace().addCollisionListener(colCont);
        sim.getBulletAppState().getPhysicsSpace().add(viewCont);
        sim.getBulletAppState().getPhysicsSpace().addCollisionListener(viewCont);
    }
    
    /**
     * Disable Collision
     */
    public void disableCol(){
        sim.getBulletAppState().getPhysicsSpace().removeCollisionListener(colCont);
        sim.getBulletAppState().getPhysicsSpace().remove(colCont);
        sim.getBulletAppState().getPhysicsSpace().removeCollisionListener(viewCont);
        sim.getBulletAppState().getPhysicsSpace().remove(viewCont);
    }
    
    /**
     *
     * @return splitTime
     */
    public float getSplitTime(){
        return splitTime;
    }
    
    /**
     * Reset the splitTime
     */
    public void resetSplitTime(){
        splitTime = 10.0f;
    }
    
    /**
     *
     * @return Size of the swarm
     */
    public int getSize(){
        return swarm.size();
    }
    
    /**
     *
     * @return Location of the collision from the colControl
     */
    public Vector3f getColLocation(){
        return colLocation;
    }
    
    /**
     *
     * @return Location of the collision from the viewControl
     */
    public Vector3f getViewLocation(){
        return viewLocation;
    }
    
    /**
     *
     * @return size of the swarm
     */
    public int size(){
        return swarm.size();
    }
    
    private float getGaussianDistributionNoise(float StandardDeviation){
         float rand = (float)((random.nextGaussian()*(StandardDeviation)));
         return rand;
     }

    @Override
    public Vector3f getNearestLocation(Vector3f location) {
        return center.add(location.subtract(center).normalize().mult(colRadius));
    }
    
    @Override
    public void addToMap(FoodSourceMap foreignMap){
        if(foreignMaps == null){
            foreignMaps = new ArrayList<FoodSourceMap>();
        }
        foreignMaps.add(foreignMap);
    }

    @Override
    public float feed(Vector3f location, float amount) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return 0;
    }
}
