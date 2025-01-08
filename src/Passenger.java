import bagel.*;

class Passenger extends Entity {
    //Passenger Flags
    private boolean isInTaxi = false;
    private boolean movingToFlag = false;
    private boolean tripComplete = false;
    private boolean hasCoin = false;
    boolean isEjected = false;
    private boolean wasOnTripWhenEjected = false;
    private boolean shouldRenderBlood = false;
    private boolean isColliding = false;

    //Use this for Flag X,Y Coordinates
    private int endX;
    private int yDistance;

    //Passenger Earnings (Priority)
    private double expectedEarning;
    private int originalPriority;
    private int currentPriority;
    private boolean hasUmbrella = false;

    //Passenger Variables
    private final int PASSENGERFLAGMOVESPEED = Integer.parseInt(getGameProps().getProperty("gameObjects.taxi.speedX"));
    private final int PASSENGERMOVESPEED = Integer.parseInt(getGameProps().getProperty("gameObjects.taxi.speedY"));
    private final int PassengerXMoveSpeed = Integer.parseInt(getGameProps().getProperty("gameObjects.passenger.walkSpeedX"));
    private final int PassengerYMoveSpeed = Integer.parseInt(getGameProps().getProperty("gameObjects.passenger.walkSpeedX"));
    private double INITIAL_HEALTH = Double.parseDouble(getGameProps().getProperty("gameObjects.passenger.health")) * 100;

    //Entity
    private TripEndFlag tripEndFlag;
    private Driver driver;

    //Frames
    private int collisionTimeoutFrames = 0;
    private int collisionMoveFrames = 0;
    private int collisionDirectionX = 0;
    private int collisionDirectionY = 0;
    private int bloodFramesDuration = 0;
    private final int BLOOD_DURATION = Integer.parseInt(getGameProps().getProperty("gameObjects.blood.ttl"));;

    /**
     * Constructs a Passenger object with specified coordinates, priority, destination, and attributes.
     *
     * @param x            the x-coordinate of the passenger
     * @param y            the y-coordinate of the passenger
     * @param priority     the priority of the passenger
     * @param endX         the destination x-coordinate
     * @param yDistance    the y-distance to the destination
     * @param hasUmbrella  whether the passenger has an umbrella
     * @param image        the image representing the passenger
     */
    public Passenger(double x, double y, int priority, int endX, int yDistance, boolean hasUmbrella, Image image) {
        super(x, y, image);
        this.originalPriority = priority;
        this.currentPriority = priority;
        this.endX = endX;
        this.yDistance = yDistance;
        this.hasUmbrella = hasUmbrella;
        this.expectedEarning = calculateExpectedEarning();
        this.health = INITIAL_HEALTH;
    }
    /**
     * Gets the expected earning of the passenger based on their distance and priority.
     *
     * @return the expected earning
     */
    public double getExpectedEarning() {
        return expectedEarning;
    }
    /**
     * Gets the passenger's movement speed along the X-axis.
     *
     * @return the movement speed along the X-axis
     */
    public int getPassengerXMoveSpeed() {
        return PassengerXMoveSpeed;
    }

    /**
     * Gets the passenger's movement speed along the Y-axis.
     *
     * @return the movement speed along the Y-axis
     */
    public int getPassengerYMoveSpeed() {
        return PassengerYMoveSpeed;
    }
    /**
     * Checks if the passenger has collected a coin.
     *
     * @return true if the passenger has a coin, false otherwise
     */
    public boolean isHasCoin() {
        return hasCoin;
    }
    /**
     * Checks if the passenger is currently moving towards the trip end flag.
     *
     * @return true if the passenger is moving to the flag, false otherwise
     */
    public boolean MovingToFlag() {
        return movingToFlag;
    }
    /**
     * Checks if the passenger is currently in a taxi.
     *
     * @return true if the passenger is in the taxi, false otherwise
     */
    public boolean isInTaxi() {
        return isInTaxi;
    }
    /**
     * Checks if the passenger is currently colliding with another entity.
     *
     * @return true if the passenger is colliding, false otherwise
     */
    public boolean isColliding() {
        return isColliding;
    }
    /**
     * Checks if the passenger has been ejected from the taxi.
     *
     * @return true if the passenger has been ejected, false otherwise
     */
    public boolean isEjected() {
        return isEjected;
    }
    /**
     * Sets the passenger's collision state.
     *
     * @param colliding true if the passenger is colliding, false otherwise
     */
    public void setColliding(boolean colliding) {
        isColliding = colliding;
    }
    /**
     * Assigns a driver to the passenger.
     *
     * @param driver the driver of the passenger
     */
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
    /**
     * Sets the number of frames during which the passenger will remain in a collision state.
     *
     * @param collisionTimeoutFrames the number of frames for the collision timeout
     */
    public void setCollisionTimeoutFrames(int collisionTimeoutFrames) {
        this.collisionTimeoutFrames = collisionTimeoutFrames;
    }
    /**
     * Gets the current priority of the passenger.
     *
     * @return the current priority
     */
    public int getPriority() {
        return currentPriority;
    }
    /**
     * Checks if the passenger has completed their trip.
     *
     * @return true if the trip is complete, false otherwise
     */
    public boolean getTripComplete() {
        return tripComplete;
    }
    /**
     * Marks the passenger's trip as complete or incomplete.
     *
     * @param tripComplete true if the trip is complete, false otherwise
     */
    public void setTripComplete(boolean tripComplete) {
        this.tripComplete = tripComplete;
    }
    /**
     * Sets the flag that indicates the trip end for the passenger.
     *
     * @param tripEndFlag the trip end flag to set
     */
    public void setTripEndFlag(TripEndFlag tripEndFlag) {
        this.tripEndFlag = tripEndFlag;
    }
    /**
     * Sets whether the passenger is currently moving towards the trip end flag.
     *
     * @param movingToFlag true if the passenger is moving to the flag, false otherwise
     */
    public void setMovingToFlag(boolean movingToFlag) {
        this.movingToFlag = movingToFlag;
    }
    /**
     * Gets the passenger's destination X-coordinate.
     *
     * @return the destination X-coordinate
     */
    public int getEndX() {
        return endX;
    }
    /**
     * Gets the Y-distance to the passenger's destination.
     *
     * @return the Y-distance to the destination
     */
    public int getyDistance() {
        return yDistance;
    }
    /**
     * Sets whether the passenger is currently in a taxi.
     *
     * @param inTaxi true if the passenger is in the taxi, false otherwise
     */
    public void setInTaxi(boolean inTaxi) {
        isInTaxi = inTaxi;
    }
    /**
     * Gets the trip end flag for the passenger's trip.
     *
     * @return The TripEndFlag for the current trip, or null if none is set.
     */
    public TripEndFlag getTripEndFlag(){
        return this.tripEndFlag;
    }
    /**
     * Checks if the passenger was on a trip when ejected from the taxi.
     *
     * @return true if the passenger was on a trip when ejected, false otherwise
     */
    public boolean wasOnTripWhenEjected() {
        return wasOnTripWhenEjected;
    }
    /**
     * Sets the direction of the collision and the number of frames to move the passenger away from the collision.
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
     * Gets the priority rate based on the passenger's priority.
     *
     * @param priority the priority of the passenger
     * @return the rate associated with the priority
     */
    private int getPriorityRate(int priority) {
        switch (priority) {
            case 1:
                return 50;
            case 2:
                return 20;
            case 3:
                return 10;
            default:
                return 0;
        }
    }
    /**
     * Ejects the passenger from the taxi and places them next to the taxi.
     *
     * @param taxi the taxi from which the passenger is ejected
     */
    public void ejectFromTaxi(Taxi taxi) {
        setX(taxi.getX() - 100);  // Eject to the left of the taxi
        setY(taxi.getY());
        isInTaxi = false;
        isEjected = true;
        wasOnTripWhenEjected = !tripComplete;  // Set this flag if the trip wasn't complete
        this.driver = taxi.getDriver();
    }
    /**
     * Adjusts the passenger's priority based on the current weather conditions.
     * If it's raining and the passenger doesn't have an umbrella, their priority increases.
     *
     * @param weatherCondition the current weather condition
     */
    public void adjustPriorityForWeather(String weatherCondition) {
        if (isInTaxi) {
            return; // Don't adjust priority if already in taxi
        }
        if (weatherCondition.equals("RAINING") && !hasUmbrella) {
            currentPriority = 1;
        } else {
            currentPriority = originalPriority;
        }
        this.expectedEarning = calculateExpectedEarning();
    }
    /**
     * Increases the passenger's priority, indicating they have collected a coin.
     */
    public void increasePriority() {
        if (currentPriority > 1) {
            currentPriority--;
            hasCoin = true;
        }
        this.expectedEarning = calculateExpectedEarning();
    }
    /**
     * Calculates the expected earnings for the passenger based on distance and priority.
     *
     * @return the calculated expected earnings
     */
    private double calculateExpectedEarning() {
        double distanceFee = yDistance * 0.1;
        double priorityFee = currentPriority * getPriorityRate(currentPriority);
        return distanceFee + priorityFee;
    }
    /**
     * Applies damage to the passenger, reducing their health.
     * If the health reaches 0, the passenger's state changes to show bleeding effects.
     *
     * @param amount the amount of damage to apply
     */
    public void damage(double amount) {
        if (collisionTimeoutFrames == 0) {
            this.health = Math.max(0, this.health - amount);
            if (this.health <= 0) {
                shouldRenderBlood = true;
                bloodFramesDuration = BLOOD_DURATION;
            }
            collisionTimeoutFrames = 200;
        }
    }

    /**
     * Updates the passenger's state based on input or collision status.
     *
     * @param input the user input
     */
    public void update(Input input) {
        if (isEjected && driver != null) {
            // Follow the driver's movements
            if (getX() < driver.getX()) {
                setX(getX() + 1);  // Move right towards the driver
            } else if (getX() > driver.getX()) {
                setX(getX() - 1);  // Move left towards the driver
            }
            // Move on the Y axis
            if (getY() < driver.getY()) {
                setY(getY() + 1);  // Move down towards the driver
            } else if (getY() > driver.getY()) {
                setY(getY() - 1);  // Move up towards the driver
            }
        } else if (movingToFlag) {
            if (getX() < tripEndFlag.getX()) {
                setX(getX() + PASSENGERFLAGMOVESPEED);
            } else if (getX() > tripEndFlag.getX()) {
                setX(getX() - PASSENGERFLAGMOVESPEED);
            }
            if (getY() < tripEndFlag.getY()) {
                setY(getY() + PASSENGERFLAGMOVESPEED);
            } else if (getY() > tripEndFlag.getY()) {
                setY(getY() - PASSENGERFLAGMOVESPEED);
            }
            if (getY() == tripEndFlag.getY() && getX() == tripEndFlag.getX()) {
                movingToFlag = false;
                tripEndFlag.setTripCompleted(true);
            }
            return;
        }
        if (input.isDown(Keys.UP) && !isInTaxi) {
            setY(getY() + PASSENGERMOVESPEED);
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
     * Draws the passenger on the screen unless they are in the taxi.
     * If the passenger has been injured, renders blood effects.
     */
    @Override
    public void draw() {
        if (!isInTaxi) {
            super.draw();
            if (shouldRenderBlood && bloodFramesDuration > 0) {
                blood.draw(getX(), getY());
            }
        }
    }
}
