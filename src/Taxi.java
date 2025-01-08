import bagel.*;

class Taxi extends Entity {
    //Taxi Variables
    private final double TAXI_RADIUS = Double.parseDouble(getGameProps().getProperty("gameObjects.taxi.radius"));
    private int TAXI_SPEED_X = Integer.parseInt(getGameProps().getProperty("gameObjects.taxi.speedX"));
    private double INITIAL_HEALTH = Double.parseDouble(getGameProps().getProperty("gameObjects.taxi.health")) * 100;

    //Background
    private final int BACKGROUND_SCROLL_SPEED = 5;

    //Made boundaries so the taxi doesn't go off the road
    private final double roadLeftBound = 360;
    private final double roadRightBound = Window.getWidth() - 360;

    //Taxi Flags
    private boolean hasPassenger = false;
    private boolean moving = false;
    private boolean permanentlyDamaged = false;
    private boolean shouldRenderSmoke = false;
    private boolean shouldRenderFire = false;
    private boolean isInvincible = false;
    private boolean isColliding = false;

    //Entity
    private Driver driver;
    private Passenger passenger;
    private Passenger ejectedPassenger;

    //Images
    private Image normalTaxi;
    private Image damagedTaxi;
    private Image fireImage = new Image("res/fire.png");


    //Frames/Collision
    private int invincibilityFramesLeft = 0;
    private int smokeFramesDuration;
    private int collisionTimeoutFrames = 0;
    private int collisionMoveFrames = 0;
    private int collisionDirectionX = 0;
    private int collisionDirectionY = 0;
    private int fireFramesDuration = 0;
    private final int EFFECT_DURATION = Integer.parseInt(getGameProps().getProperty("gameObjects.smoke.ttl"));;

    /**
     * Constructs a Taxi object with the specified coordinates, normal image, and damaged image.
     *
     * @param x the x-coordinate of the taxi
     * @param y the y-coordinate of the taxi
     * @param image the image representing the normal state of the taxi
     * @param damagedTaxi the image representing the damaged state of the taxi
     */
    public Taxi(double x, double y, Image image, Image damagedTaxi) {
        super(x, y, image);
        this.normalTaxi = image;
        this.health = INITIAL_HEALTH;
        this.damagedTaxi = damagedTaxi;
    }

    //Getters Setters
    /**
     * Checks if the taxi is currently colliding.
     *
     * @return true if the taxi is colliding, false otherwise
     */
    public boolean isColliding() {
        return isColliding;
    }
    /**
     * Sets the taxi's colliding state.
     *
     * @param colliding true if the taxi is colliding, false otherwise
     */
    public void setColliding(boolean colliding) {
        isColliding = colliding;
    }
    /**
     * Sets the collision timeout frames for the taxi, indicating how long the taxi
     * should remain in a colliding state.
     *
     * @param collisionTimeoutFrames the number of frames for the collision timeout
     */
    public void setCollisionTimeoutFrames(int collisionTimeoutFrames) {
        this.collisionTimeoutFrames = collisionTimeoutFrames;
    }
    /**
     * Gets the current passenger in the taxi.
     *
     * @return the current passenger, or null if no passenger is present
     */
    public Passenger getPassenger() {
        return passenger;
    }
    /**
     * Gets the radius of the taxi, used for collision detection.
     *
     * @return the radius of the taxi
     */
    public double getTaxiRadius() {
        return TAXI_RADIUS;
    }
    /**
     * Sets the passenger in the taxi.
     *
     * @param passenger the passenger to set
     */
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
    /**
     * Sets whether the taxi has a passenger.
     *
     * @param hasPassenger true if the taxi has a passenger, false otherwise
     */
    public void setHasPassenger(boolean hasPassenger) {
        this.hasPassenger = hasPassenger;
    }
    /**
     * Checks if the taxi is currently moving.
     *
     * @return true if the taxi is moving, false otherwise
     */
    public boolean isMoving() {
        return moving;
    }
    /**
     * Checks if the taxi currently has a passenger.
     *
     * @return true if the taxi has a passenger, false otherwise
     */
    public boolean getHasPassenger() {
        return hasPassenger;
    }
    /**
     * Gets the current health of the taxi.
     *
     * @return the health of the taxi
     */
    public double getHealth() {
        return health;
    }
    /**
     * Sets the health of the taxi, ensuring it is between 0 and the initial health.
     *
     * @param health the new health value
     */
    public void setHealth(double health) {
        super.setHealth(Math.max(0, Math.min(health, INITIAL_HEALTH)));
    }
    /**
     * Sets the direction of the taxi after a collision.
     *
     * @param directionX the x-direction of the collision
     * @param directionY the y-direction of the collision
     */
    public void setCollisionDirection(int directionX, int directionY) {
        this.collisionDirectionX = directionX;
        this.collisionDirectionY = directionY;
        this.collisionMoveFrames = 10;
    }
    /**
     * Sets the driver of the taxi and links the driver with the taxi.
     *
     * @param driver the driver to set
     */
    public void setDriver(Driver driver) {
        this.driver = driver;
        if (driver != null) {
            driver.setCurrentTaxi(this);
            driver.setInTaxi(true);
        }
    }
    /**
     * Checks if the taxi is permanently damaged (i.e., cannot be used anymore).
     *
     * @return true if the taxi is permanently damaged, false otherwise
     */
    public boolean isPermanentlyDamaged() {
        return permanentlyDamaged;
    }
    /**
     * Returns the passenger that was ejected from the taxi.
     * <p>
     * @return The {@link Passenger} object representing the ejected passenger,
     *         or {@code null} if no passenger was ejected.
     */
    public Passenger getEjectedPassenger() {
        return ejectedPassenger;
    }
    /**
     * Gets the current driver of the taxi.
     *
     * @return the current driver, or null if no driver is present
     */
    public Driver getDriver() {
        return this.driver;
    }
    /**
     * Makes the taxi invincible for a specified duration.
     *
     * @param duration the number of frames the taxi remains invincible
     */
    public void makeInvincible(int duration) {
        isInvincible = true;
        invincibilityFramesLeft = duration;
    }
    /**
     * Damages the taxi by a specified amount, unless the taxi is invincible or permanently damaged.
     * If the health reaches zero, the taxi becomes permanently damaged.
     *
     * @param amount the amount of damage to apply
     */
    public void damage(double amount) {
        if (!isInvincible && !permanentlyDamaged && collisionTimeoutFrames == 0) {
            this.health -= amount;
            if (this.health <= 0) {
                this.health = 0;
                handlePermanentDamage();
            } else {
                shouldRenderSmoke = true;
                smokeFramesDuration = EFFECT_DURATION;
            }
            //No app property for this
            collisionTimeoutFrames = 200;
        }
    }
    //Handles the permanent damage state of the taxi
    private void handlePermanentDamage() {
        if (!permanentlyDamaged) {
            permanentlyDamaged = true;
            fireFramesDuration = EFFECT_DURATION;
            shouldRenderFire = true;
            if (passenger != null) {
                ejectedPassenger = passenger; // Store the reference to the ejected passenger
                passenger.ejectFromTaxi(this);
                passenger.setDriver(this.driver);
                passenger = null; // Clear the passenger from the taxi
            }
            if (driver != null) {
                driver.ejectFromTaxi();
            }
        }
    }
    /**
     * Updates the state of the taxi based on user input and the current game state.
     * This method handles movement, collision, and invincibility status.
     *
     * @param input the user input for controlling the taxi
     */
    @Override
    public void update(Input input) {
        moving = false;
        if (health <= 0) {
            handlePermanentDamage();
        }
        if (permanentlyDamaged) {
            if (fireFramesDuration > 0) {
                fireFramesDuration--;
                shouldRenderFire = true;
            } else {
                shouldRenderFire = false;
            }
            //Counteract the scrolling background
            if (input.isDown(Keys.UP)) {
                setY(getY() + TAXI_SPEED_X * 3);
            }
        }
        else if (driver != null) {
            // Normal movement when driver is present
            if (input.isDown(Keys.LEFT) && getX() > roadLeftBound) {
                setX(getX() - TAXI_SPEED_X);
                moving = true;
                if (passenger != null) {
                    passenger.setX(getX());
                }
            }
            else if (input.isDown(Keys.RIGHT) && getX() < roadRightBound - normalTaxi.getWidth() / 2) {
                setX(getX() + TAXI_SPEED_X);
                moving = true;
                if (passenger != null) {
                    passenger.setX(getX());
                }
            }
            if (input.isDown(Keys.UP)) {
                moving = true;
            }
        } else {
            // Counteract background scrolling when there's no driver
            if (input.isDown(Keys.UP)) {
                setY(getY() + BACKGROUND_SCROLL_SPEED);
            }
        }

        // Update passenger position if present
        if (passenger != null) {
            passenger.setX(getX());
            passenger.setY(getY());
        }

        if (isInvincible) {
            invincibilityFramesLeft--;
            if (invincibilityFramesLeft <= 0) {
                isInvincible = false;
            }
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

        if (smokeFramesDuration > 0) {
            smokeFramesDuration--;
        } else {
            shouldRenderSmoke = false;
        }
    }
    /**
     * Draws the taxi, and renders smoke or fire effects if the taxi is damaged.
     */
    public void draw() {
        if (permanentlyDamaged) {
            damagedTaxi.draw(getX(), getY());
        } else {
            normalTaxi.draw(getX(), getY());
        }
        if (shouldRenderFire) {
            fireImage.draw(getX(), getY());
        }
        if (shouldRenderSmoke && smokeFramesDuration > 0) {
            smoke.draw(getX(), getY());
        }
    }
}