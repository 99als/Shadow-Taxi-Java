import bagel.*;
class InvinciblePower extends Entity {
    //Variables
    private final double RADIUS = Double.parseDouble(getGameProps().getProperty("gameObjects.invinciblePower.radius"));
    private static final int MOVE_SPEED = 5;
    private final int INVINCIBILITY_DURATION = Integer.parseInt(getGameProps().getProperty("gameObjects.invinciblePower.maxFrames"));
    //Flag
    private boolean isCollided = false;

    /**
     * Constructs an InvinciblePower object at the specified coordinates with the given image.
     * This object grants invincibility to entities that collide with it.
     *
     * @param x the x-coordinate of the invincibility power-up
     * @param y the y-coordinate of the invincibility power-up
     * @param image the image representing the invincibility power-up
     */
    public InvinciblePower(double x, double y, Image image) {
        super(x, y, image);
    }
    /**
     * Gets the radius of the invincibility power-up, used for collision detection.
     *
     * @return the radius of the power-up
     */
    public double getRadius() {
        return RADIUS;
    }
    /**
     * Sets the collision state of the invincibility power-up.
     * Once collided, the power-up will no longer interact with entities.
     *
     * @param collided true if the power-up has collided, false otherwise
     */
    public void setCollided(boolean collided) {
        isCollided = collided;
    }
    /**
     * Checks whether the invincibility power-up has collided with another entity.
     *
     * @return true if the power-up has collided, false otherwise
     */
    public boolean isCollided() {
        return isCollided;
    }
    /**
     * Gets the duration for which the invincibility power-up grants invincibility.
     *
     * @return the duration of invincibility in frames
     */
    public int getInvincibilityDuration() {
        return INVINCIBILITY_DURATION;
    }

    /**
     * Updates the position of the invincibility power-up based on user input.
     * If the UP key is pressed and the power-up hasn't collided, it moves downward.
     *
     * @param input the user input for controlling the movement of the power-up
     */
    @Override
    public void update(Input input) {
        if (!isCollided && input.isDown(Keys.UP)) {
            setY(getY() + MOVE_SPEED);
        }
    }

    /**
     * Draws the invincibility power-up on the screen if it hasn't collided.
     */
    @Override
    public void draw() {
        if (!isCollided) {
            super.draw();
        }
    }
}