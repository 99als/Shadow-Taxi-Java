import bagel.*;
class Driver extends Entity {
    //Variables
    private final int MOVE_SPEED = Integer.parseInt(getGameProps().getProperty("gameObjects.driver.walkSpeedY"));
    private double INITIAL_HEALTH = Double.parseDouble(getGameProps().getProperty("gameObjects.driver.health")) * 100;
    private int radius = Integer.parseInt(getGameProps().getProperty("gameObjects.driver.radius"));
    //Flags
    private boolean inTaxi = true;
    private boolean isInvincible = false;
    private boolean isColliding = false;
    private boolean shouldRenderBlood = false;
    //Entity
    private Taxi currentTaxi;
    //Frames
    private int invincibilityFramesLeft = 0;
    private int collisionTimeoutFrames = 0;
    private int collisionMoveFrames = 0;
    private int collisionDirectionX = 0;
    private int collisionDirectionY = 0;
    private int bloodFramesDuration = 0;
    private final int BLOOD_DURATION = Integer.parseInt(getGameProps().getProperty("gameObjects.blood.ttl"));;

    //Image
    private final Image blood = new Image("res/blood.png");

    /**
     * Constructs a Driver object at the specified coordinates with the given image.
     * Initializes the driver's health to the initial value.
     *
     * @param x the x-coordinate of the driver
     * @param y the y-coordinate of the driver
     * @param image the image representing the driver
     */
    public Driver(double x, double y, Image image) {
        super(x, y, image);
        this.health = INITIAL_HEALTH;
    }
    /**
     * Checks if the driver is currently colliding with another entity.
     *
     * @return true if the driver is colliding, false otherwise
     */
    public boolean isColliding() {
        return isColliding;
    }
    /**
     * Sets the driver's collision state.
     *
     * @param colliding true if the driver is colliding, false otherwise
     */
    public void setColliding(boolean colliding) {
        isColliding = colliding;
    }
    /**
     * Sets the number of frames during which the driver remains in a collision state.
     *
     * @param collisionTimeoutFrames the number of frames for the collision timeout
     */
    public void setCollisionTimeoutFrames(int collisionTimeoutFrames) {
        this.collisionTimeoutFrames = collisionTimeoutFrames;
    }
    /**
     * Makes the driver invincible for a specified duration.
     * The driver cannot take damage while invincible.
     *
     * @param duration the duration of invincibility in frames
     */
    public void makeInvincible(int duration) {
        isInvincible = true;
        invincibilityFramesLeft = duration;
    }
    /**
     * Checks if the driver is currently invincible.
     *
     * @return true if the driver is invincible, false otherwise
     */
    public boolean isInvincible() {
        return isInvincible;
    }
    /**
     * Sets whether the driver is inside a taxi.
     *
     * @param inTaxi true if the driver is in a taxi, false otherwise
     */
    public void setInTaxi(boolean inTaxi) {
        this.inTaxi = inTaxi;
    }
    /**
     * Checks if the driver is inside a taxi.
     *
     * @return true if the driver is in a taxi, false otherwise
     */
    public boolean isInTaxi() {
        return inTaxi;
    }
    /**
     * Associates the driver with the current taxi.
     *
     * @param taxi the taxi to associate with the driver
     */
    public void setCurrentTaxi(Taxi taxi) {
        this.currentTaxi = taxi;
    }
    /**
     * Gets the radius of the driver, used for collision detection.
     *
     * @return the radius of the driver
     */
    public int getRadius(){
        return this.radius;
    }
    /**
     * Places the driver inside the specified taxi and moves the driver to the taxi's position.
     *
     * @param taxi the taxi the driver is entering
     */
    public void enterTaxi(Taxi taxi) {
        this.currentTaxi = taxi;
        this.inTaxi = true;
        taxi.setDriver(this);
        setX(taxi.getX());
        setY(taxi.getY());
        if (isInvincible){
            taxi.makeInvincible(invincibilityFramesLeft);
        }
    }
    /**
     * Checks if the driver is close enough to the taxi to enter it.
     *
     * @param taxi the taxi to check proximity to
     * @return true if the driver is close enough to enter the taxi, false otherwise
     */
    public boolean canEnterTaxi(Taxi taxi) {
        double distance = calculateEuclideanDistance(getX(), getY(), taxi.getX(), taxi.getY());
        return distance <= 10;
    }
    /**
     * Calculates the Euclidean distance between two points.
     * This method is private since it is a helper function used internally.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the Euclidean distance between the two points
     */
    private double calculateEuclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    /**
     * Ejects the driver from the current taxi, placing the driver next to the taxi.
     * This also removes the driver's association with the taxi.
     */
    public void ejectFromTaxi() {
        if (currentTaxi != null) {
            // Set driver outside the taxi after ejection
            setX(currentTaxi.getX() - 50);  // Eject to the left of the taxi
            setY(currentTaxi.getY());
            inTaxi = false;  // Mark that the driver is no longer in the taxi
            currentTaxi = null;  // Remove association with the taxi
        }
    }
    /**
     * Sets the direction of the collision and how far the driver moves away from the collision.
     *
     * @param directionX the X direction of the collision
     * @param directionY the Y direction of the collision
     */
    public void setCollisionDirection(int directionX, int directionY) {
        this.collisionDirectionX = directionX * 2;
        this.collisionDirectionY = directionY * 2;
        this.collisionMoveFrames = 10;
    }
    /**
     * Applies damage to the driver, reducing their health.
     * If the driver's health reaches 0, the blood effect is triggered.
     *
     * @param amount the amount of damage to apply
     */
    @Override
    public void damage(double amount) {
        if (!isInvincible && collisionTimeoutFrames == 0) {
            this.health = Math.max(0, this.health - amount);
            if (this.health <= 0) {
                shouldRenderBlood = true;
                bloodFramesDuration = BLOOD_DURATION;
            }
            collisionTimeoutFrames = 200;
        }
    }

    /**
     * Updates the driver's state, including movement and collision handling.
     * If the driver is inside a taxi, their position is updated to follow the taxi.
     * Handles input for moving the driver when they are not in a taxi.
     *
     * @param input the user input for controlling the driver
     */
    @Override
    public void update(Input input) {
        if (inTaxi && currentTaxi != null) {
            // If the driver is inside the taxi, follow the taxi's position
            setX(currentTaxi.getX());
            setY(currentTaxi.getY());
        }
        if (!inTaxi) {
            if (input.isDown(Keys.UP)){
                setY(getY() - MOVE_SPEED);
            }
            if (input.isDown(Keys.DOWN)){
                setY(getY() + MOVE_SPEED);
            }
            if (input.isDown(Keys.LEFT)){
                setX(getX() - MOVE_SPEED);
            }
            if (input.isDown(Keys.RIGHT)){
                setX(getX() + MOVE_SPEED);
            }
        } else if (currentTaxi != null) {
            setX(currentTaxi.getX());
            setY(currentTaxi.getY());
        }
        if(isInvincible) {
            invincibilityFramesLeft--;
            if (invincibilityFramesLeft <= 0) {
                isInvincible = false;
            }
        }
        if (bloodFramesDuration > 0) {
            bloodFramesDuration--;
        }
        if (collisionTimeoutFrames > 0) {
            collisionTimeoutFrames--;
            if (collisionMoveFrames > 0) {
                setX(getX() + collisionDirectionX);
                setY(getY() + collisionDirectionY);
                collisionMoveFrames--;
            }
        }
        if (collisionTimeoutFrames == 0) {
            isColliding = false;
        }
    }

    /**
     * Draws the driver on the screen.
     * If the driver has taken damage and the blood effect is active, it renders the blood as well.
     */
    @Override
    public void draw() {
        if (!inTaxi) {
            super.draw();
            if (shouldRenderBlood && bloodFramesDuration > 0) {
                blood.draw(getX(), getY());
            }
        }
    }
}