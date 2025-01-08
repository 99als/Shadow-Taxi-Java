import bagel.*;
class Car extends Entity {
    //Variables
    private final double RADIUS = Double.parseDouble(getGameProps().getProperty("gameObjects.otherCar.radius"));
    private double INITIAL_HEALTH = Double.parseDouble(getGameProps().getProperty("gameObjects.otherCar.health")) * 100;
    private final double DAMAGE = Double.parseDouble(getGameProps().getProperty("gameObjects.otherCar.damage")) * 100;
    private int speed;
    //Flags
    private boolean shouldRender = true;
    private boolean isColliding = false;
    private boolean shouldRenderSmoke = false;
    private boolean shouldRenderFire = false;
    //Frames
    private int collisionTimeoutFrames = 0;
    private int collisionMoveFrames = 0;
    private int collisionDirectionX = 0;
    private int collisionDirectionY = 0;
    private int effectFramesDuration = 0;
    private final int EFFECT_DURATION = 20;
    //Images
    private Image smokeImage = new Image("res/smoke.png");
    private Image fireImage = new Image("res/fire.png");

    /**
     * Constructs a Car object at the specified coordinates with the given image.
     * The speed of the car is set randomly between 2 and 5.
     *
     * @param x the x-coordinate of the car
     * @param y the y-coordinate of the car
     * @param image the image representing the car
     */
    public Car(double x, double y, Image image) {
        super(x, y, image);
        this.speed = MiscUtils.getRandomInt(2, 5); // Random speed between 2 and 5
        this.health = INITIAL_HEALTH;
    }
    /**
     * Checks if the car is currently colliding with another entity.
     *
     * @return true if the car is colliding, false otherwise
     */
    public boolean isColliding() {
        return isColliding;
    }
    /**
     * Regenerates the speed of the car after a collision.
     * The speed is randomly set between 2 and 5.
     */
    private void regenerateSpeed() {
        this.speed = MiscUtils.getRandomInt(2, 5);
    }
    /**
     * Sets the car's collision state.
     *
     * @param colliding true if the car is colliding, false otherwise
     */
    public void setColliding(boolean colliding) {
        isColliding = colliding;
    }
    /**
     * Sets the number of frames during which the car remains in a collision state.
     *
     * @param collisionTimeoutFrames the number of frames for the collision timeout
     */
    public void setCollisionTimeoutFrames(int collisionTimeoutFrames) {
        this.collisionTimeoutFrames = collisionTimeoutFrames;
    }
    /**
     * Gets the radius of the car, used for collision detection.
     *
     * @return the radius of the car
     */
    public double getRadius() {
        return RADIUS;
    }
    /**
     * Gets the amount of damage the car can deal.
     *
     * @return the damage value of the car
     */
    public double getDamage() {
        return DAMAGE;
    }
    /**
     * Sets the direction and movement of the car after a collision.
     * The car will move for 10 frames in the given direction and remain in a collision state for 200 frames.
     *
     * @param directionX the X direction of the collision
     * @param directionY the Y direction of the collision
     */
    public void setCollisionDirection(int directionX, int directionY) {
        this.collisionDirectionX = directionX;
        this.collisionDirectionY = directionY;
        this.collisionMoveFrames = 10;
        this.collisionTimeoutFrames = 200;
    }
    /**
     * Applies damage to the car, reducing its health.
     * If the health drops to 0, the car will render fire effects. Otherwise, smoke effects will be rendered.
     * The car remains in a collision state for 200 frames after taking damage.
     *
     * @param amount the amount of damage to apply
     */
    public void damage(double amount) {
        if (collisionTimeoutFrames == 0) {
            this.health -= amount;
            if (this.health <= 0) {
                this.health = 0;
                shouldRenderFire = true;
                shouldRenderSmoke = false;
                effectFramesDuration = EFFECT_DURATION;
            } else {
                shouldRenderSmoke = true;
                effectFramesDuration = EFFECT_DURATION;
            }
            collisionTimeoutFrames = 200;
        }
    }
    /**
     * Updates the car's position and handles collision effects.
     * After a collision, the car moves according to the collision direction for 10 frames.
     * If the car's health reaches 0, it will stop rendering after the fire effects are completed.
     *
     * @param input the user input (not used for the car's movement)
     */
    @Override
    public void update(Input input) {
        if (collisionTimeoutFrames > 0) {
            collisionTimeoutFrames--;
            if (collisionMoveFrames > 0) {
                setX(getX() + collisionDirectionX);
                setY(getY() + collisionDirectionY);
                collisionMoveFrames--;
            } else if (collisionTimeoutFrames == 190) { // 200 - 10 frames
                regenerateSpeed();
            }
        } else {
            isColliding = false;
        }
        if (effectFramesDuration > 0) {
            effectFramesDuration--;
        } else {
            shouldRenderSmoke = false;
            shouldRenderFire = false;
            if (health <= 0) {
                shouldRender = false;
            }
        }

        setY(getY() - speed); // Normal movement
    }
    /**
     * Draws the car on the screen.
     * If the car is damaged, it will render smoke or fire effects depending on its health.
     * The car is not rendered if it is destroyed.
     */
    @Override
    public void draw() {
        if (shouldRender) {
            super.draw();
            if (shouldRenderSmoke && effectFramesDuration > 0) {
                smokeImage.draw(getX(), getY());
            } else if (shouldRenderFire && effectFramesDuration > 0) {
                fireImage.draw(getX(), getY());
            }
        }
    }
}