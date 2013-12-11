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

public class Swarm {
    protected List<Fish> swarm;
    protected FishSim sim;
    protected Vector3f scale;
    protected float near;
    protected int id;
    protected Random random = new Random();
    protected Vector3f center;
    protected float radius;
    protected SwarmColControl colCont;
    protected SwarmViewControl viewCont;
    private Vector3f colLocation = null;
    private Vector3f viewLocation = null;
    private Vector3f splitLocation = null;
    protected int type;
    protected float moveSpeed = 1;
    protected float rotationSpeed = 1;
    private float avoidTime = 0f;
    private float targetTime = 0f;
    protected float splitTime = 0f;
    protected float escapeInc = 0;
    private Vector3f lastCenter = Vector3f.ZERO;
    private boolean collided = false;
    private boolean viewCollided = false;
    protected boolean split = false;
    protected boolean merge = false;
    private Swarm mergeWith;
    
    /**
     *
     * @param sim       Simulation
     * @param size      Size of the fish
     * @param scale
     * @param spawn     Spawnpoint of the fish
     * @param map       Foodsourcemap where the fish belongs to
     * @param type      Type of the fish
     * @param id        Id of the swarm
     */
    public Swarm(FishSim sim, int size, Vector3f scale, Vector3f spawn, FoodSourceMap map, int type, int id){
        near = (float) ((Math.log1p((float)size)) * scale.length());
        this.sim = sim;
        swarm = new ArrayList<Fish>();
        center = spawn;
        this.scale = scale;
        this.type = type;
        this.id = id;
        
        for(int i = 0; i < size; i++){
            swarm.add(new Fish(sim, scale, new Vector3f(0.0f, 0.0f, 0.0f), spawn, this, map));
            swarm.get(i).show();
        }
        initCollidable();
    }
    
    /**
     *
     * @param sim       Simulation
     * @param size      Size of the fish
     * @param spawn     Spawnpoint of the fish
     * @param map       Foodsourcemap where the fish belongs to
     * @param type      Type of the fish
     * @param id        Id of the swarm
     */
    public Swarm(FishSim sim, int size, Vector3f spawn, FoodSourceMap map, int type, int id){
        scale = new Vector3f(0.25f, 0.25f, 0.25f);
        near = (float) ((Math.log1p((float)size)) * scale.length());
        this.sim = sim;
        swarm = new ArrayList<Fish>();
        center = spawn;
        this.type = type;
        this.id = id;
        
        float rand;
        for(int i = 0; i < size; i++){
            rand = getGaussianDistributionNoise(0.1f);
            swarm.add(new Fish(sim, scale.add(rand, rand, rand), new Vector3f(0.0f, 0.0f, 0.0f), spawn, this, map));
            swarm.get(i).show();
        }
        initCollidable();
    }
    
    /**
     *
     * @param sim       Simulation
     * @param size      Size of the fish
     * @param spawn     Spawnpoint of the fish
     * @param type      Type of the fish
     * @param id        Id of the swarm
     */
    public Swarm(FishSim sim, int size, Vector3f spawn, int type, int id){
        scale = new Vector3f(0.25f, 0.25f, 0.25f);
        near = (float) ((Math.log1p((float)size)) * scale.length());
        this.sim = sim;
        swarm = new ArrayList<Fish>();
        center = spawn;
        this.type = type;
        this.id = id;
        initCollidable();
    }
    
    private void initCollidable(){
        SphereCollisionShape colSphere = new SphereCollisionShape((near + scale.length() + 1f)*0.75f);
        colCont = new SwarmColControl(colSphere, this);
        colCont.setCollisionGroup(4);
        colCont.setCollideWithGroups(1);
        colCont.setCollideWithGroups(2);
        colCont.setCollideWithGroups(3);
        colCont.setCollideWithGroups(4);
        colCont.setKinematic(true);
        SphereCollisionShape viewSphere = new SphereCollisionShape(colSphere.getRadius()*2.5f);
        viewCont = new SwarmViewControl(viewSphere, this);
        viewCont.setCollisionGroup(5);
        viewCont.setCollideWithGroups(4);
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
        
        int count = 0;
        for(int i = 0; i < swarm.size(); i++){
            if(swarm.get(i).getLocalTranslation().y < splitLocation.y){
                count++;
            }
        }
                
        Swarm split1 = new Swarm(sim, count, center, type, id);
        Swarm split2 = new Swarm(sim, swarm.size()-count, center, type, id);
                
        split1.setCollided(splitLocation);
        split1.resetSplitTime();
        split2.setCollided(splitLocation);
        split2.resetSplitTime();
                
        for(int i = 0; i < swarm.size(); i++){
            if(swarm.get(i).getLocalTranslation().y < splitLocation.y){
                split1.add(swarm.get(i));
            }else{
                split2.add(swarm.get(i));
            }
        }
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
        
        int newSize = swarm.size() + mergeWith.swarm.size();
        Vector3f newCenter = center.add(mergeWith.center).divide(2);
        Swarm merged = new Swarm(sim, newSize, newCenter, type, id);
        
        for(int i = 0; i < swarm.size(); i++){
            merged.add(swarm.get(i));
        }
        for(int i = 0; i < mergeWith.swarm.size(); i++){
            merged.add(mergeWith.swarm.get(i));
        }
        
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
    
    
    private void computeCenter(){
        lastCenter = center;
        center = new Vector3f().zero();
        for(int i = 0; i < swarm.size(); i++){
            center.addLocal(swarm.get(i).getLocalTranslation());
        }
        center.divideLocal((float) swarm.size());
    }
    
    private void computeRadius(){
        radius = 0;
        float temp;
        for(int i = 0; i < swarm.size(); i++){
            temp = center.distance(swarm.get(i).getLocalTranslation());
            if(radius < temp){
                radius = temp;
            }
        }
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
}
