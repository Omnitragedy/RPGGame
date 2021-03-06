/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mygdx.Sprites.Enemies;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.Sprites.State;
import com.mygdx.game.RPGGame;
import com.mygdx.game.Screens.PlayScreen;

/**
 *
 * @author Saurav
 */
public class Grunt extends Enemy implements Steerable<Vector2> {
    
    private int hp;
    
    private float stateTimer;
    private int walkDir;
    
    public State currentState;
    public State previousState;
    
    private TextureRegion Standing;
    
    private Animation WalkUp, WalkRight, WalkDown, WalkLeft;
    
    //the instance variables below are used for the AI calculations;
    private boolean isTagged;
    private float orientationAngle;
    private float zeroLinearSpeedThreshold;
    private Vector2 currentVelocity;
    private float maxLinearSpeed, maxLinearAcceleration;
    private float maxAngularSpeed, maxAngularAcceleration;
    private boolean independentFacing;
    
    private static SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
    private SteeringBehavior<Vector2> steeringBehavior;
    
    public Grunt(PlayScreen initScreen, float x, float y, float initMaxLinSpeed) {
        super(initScreen, "GruntSprites");
        
        System.out.println(initScreen.getAtlas());
        
        isTagged = false;
        orientationAngle = (float) (3 * Math.PI / 2);
        zeroLinearSpeedThreshold = 5;
        currentVelocity = new Vector2();
        maxLinearSpeed = initMaxLinSpeed;
        maxLinearAcceleration = maxLinearSpeed;
        maxAngularSpeed = Float.MAX_VALUE;
        maxAngularAcceleration = maxAngularSpeed;
        independentFacing = true;
        
        walkDir = 3;
        
        setGrunt(x, y);
        
        
        Array<TextureRegion> frames = new Array<TextureRegion>();   //used as temporary storage for animation frames
        
        //set the "Walk Up" animation
        for(int i = 1; i < 8; i++)
            frames.add(new TextureRegion(getTexture(), i * 64, 8 * 64, 64, 64));    //param: texture, x, y, width, height
        WalkUp = new Animation(0.12f, frames);
        frames.clear();
        
        //set the "Walk Left" animation
        for(int i = 1; i < 8; i++) 
            frames.add(new TextureRegion(getTexture(), i * 64, 9 * 64, 64, 64));    //param: texture, x, y, width, height
        WalkLeft = new Animation(0.12f, frames);
        frames.clear();
        
        //set the "Walk Down" animation
        for(int i = 1; i < 8; i++) 
            frames.add(new TextureRegion(getTexture(), i * 64, 10 * 64, 64, 64));    //param: texture, x, y, width, height
        WalkDown = new Animation(0.12f, frames);
        frames.clear();
        
        //set the "Walk Right" animation
        for(int i = 1; i < 8; i++) 
            frames.add(new TextureRegion(getTexture(), i * 64, 11 * 64, 64, 64));    //param: texture, x, y, width, height
        WalkRight = new Animation(0.12f, frames);
        frames.clear();
        
        Standing = new TextureRegion(getTexture(), 0, 10 * 64+3, 64, 64);
        super.setBounds(0, 0, 40, 40);
        setRegion(Standing);
    }
    
    /**
     * Makes the grunt's b2body and sets the location of the grunt at (x,y)
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    private void setGrunt(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);      //spawns the Grunt at this position
        bdef.type = BodyDef.BodyType.DynamicBody;
        
        b2body = world.createBody(bdef);
        
        
        FixtureDef fdef = new FixtureDef();
        PolygonShape feet = new PolygonShape();
        feet.setAsBox(8, 5, new Vector2(0, -16), 0);
        fdef.shape = feet;
        
        fdef.filter.categoryBits = RPGGame.ENEMY_BIT;
        fdef.filter.maskBits = RPGGame.LEDGE_BIT |
                RPGGame.BARREL_BIT |
                RPGGame.ENEMY_BIT |
                RPGGame.HERO_BIT;

        
        fdef.restitution = 5;
        b2body.createFixture(fdef).setUserData(this);
    }
    
    /**
     * Returns the Vector indicating linear velocity
     * @return Vector indicating linear velocity
     */
    @Override
    public Vector2 getLinearVelocity() {
        return b2body.getLinearVelocity();
    }

    
    @Override
    public float getAngularVelocity() {
        return b2body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return 6;
    }

    @Override
    public boolean isTagged() {
        return isTagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        isTagged = tagged;
    }

    @Override
    public Vector2 getPosition() {
        return b2body.getPosition();
    }

    @Override
    public float getOrientation() {
        return orientationAngle;
    }

    @Override
    public void setOrientation(float orientation) {
        orientationAngle = orientation;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    

    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        zeroLinearSpeedThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Location<Vector2> newLocation() {
        return (Location<Vector2>) b2body.getPosition();
    }
    
    @Override
    public void update(double deltaT, Vector2 HeroLoc) {
//        stateTime = currentState == previousState ? stateTime + (float) deltaT : 0;
        if (steeringBehavior != null) { //makes sure that a steering behavior has actually been set
            // Calculate steering acceleration
            steeringBehavior.calculateSteering(steeringOutput);
            
            // Apply steering acceleration to move this agent
            applySteering();
        }
        
        super.setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        
        
        setRegion(getFrame(deltaT));    //sets the appropriate sprite
        
        if(HeroLoc.dst(this.getPosition()) > 250) {
            if(b2body.isAwake())
                b2body.setAwake(false);
        } else {
            if(!b2body.isAwake())
                b2body.setAwake(true);
        }
    }
    
    public TextureRegion getFrame(double deltaT) {
        currentState = getState();
        
        TextureRegion region;
        
        switch(currentState) {
            case WALK_UP:
                if(currentState != previousState)
                    Standing = new TextureRegion(getTexture(), 0, 8 * 64, 64, 64);
                region = (TextureRegion) WalkUp.getKeyFrame(stateTimer, true);
                walkDir = 1;
                break;
            case WALK_RIGHT:
                if(currentState != previousState)
                    Standing = new TextureRegion(getTexture(), 0, 11 * 64, 64, 64);
                region = (TextureRegion) WalkRight.getKeyFrame(stateTimer, true);
                walkDir = 2;
                break;
            case WALK_DOWN:
                if(currentState != previousState)
                    Standing = new TextureRegion(getTexture(), 0, 10 * 64, 64, 64);
                region = (TextureRegion) WalkDown.getKeyFrame(stateTimer, true);
                walkDir = 3;
                break;
            case WALK_LEFT:
                if(currentState != previousState)
                    Standing = new TextureRegion(getTexture(), 0, 9 * 64, 64, 64);
                region = (TextureRegion) WalkLeft.getKeyFrame(stateTimer, true);
                walkDir = 4;
                break;
            case STANDING:
            default:
                region = Standing;
                break;
        }
        
        stateTimer = currentState == previousState ? stateTimer + (float) deltaT : 0;
        previousState = currentState;
        return region;
    }
    
    private State getState() {
        currentVelocity = b2body.getLinearVelocity().cpy();
        
        if(!currentVelocity.isZero()) {
            return State.STANDING;
        }
        if(currentVelocity.x > 0) {
                walkDir = 2;
                return State.WALK_RIGHT;
            }
            else if(currentVelocity.x < 0) {
                walkDir = 4;
                return State.WALK_LEFT;
            }
            else if(currentVelocity.y > 0) {
                walkDir = 1;
                return State.WALK_UP;
            }
            else {
                walkDir = 3;
                return State.WALK_DOWN;
            }
        
    }
    
    private void applySteering () {
        Vector2 finalVector = b2body.getLinearVelocity().sub(steeringOutput.linear);    //subtract the steering vector from the current velocity
        finalVector.setLength(35);
        
        currentVelocity.set(finalVector); 
//        currentVelocity.setAngle(setSnapDirectionToGrid(vectorToAngle(currentVelocity)));
        b2body.setLinearVelocity(currentVelocity);        //make sure to uncomment
        
//        //snap the grunt movement to grid
//        Vector2 snappedVector = b2body.getLinearVelocity().sub(steering.linear);
//        snappedVector.setAngle(setSnapDirectionToGrid(snappedVector.angle()));
//        snappedVector.setLength(maxLinearSpeed);
//        b2body.setLinearVelocity(snappedVector);
    }
    
    private int setSnapDirectionToGrid(double theta) {
        double angle = theta;
        while(angle > 360)
            angle -= 360;
        
        //the sections are split into 8 pieces as there are 8 directions
        if(angle < 22.5)
            return 0;
        else if(angle >= 22.5 && angle < 67.5) 
            return 45;
        else if(angle >= 67.5 && angle < 112.5)
            return 90;
        else if(angle >= 112.5 && angle < 157.5)
            return 135;
        else if(angle >= 157.5 && angle < 202.5)
            return 180;
        else if(angle >= 202.5 && angle < 247.5)
            return 225;
        else if(angle >= 247.5 && angle < 292.5)
            return 270;
        else if(angle >= 292.5 && angle < 337.5)
            return 315;
        else if(angle >= 337.5)
            return 0;
        return 0;
    }
    
    public Body getBody() {
        return b2body;
    }
    
    public void setBehavior(SteeringBehavior<Vector2> behavior) {
        steeringBehavior = behavior;
    }
    
    public SteeringBehavior<Vector2> getBehavior() {
        return steeringBehavior;
    }
}
