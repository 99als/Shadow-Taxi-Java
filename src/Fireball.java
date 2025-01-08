import bagel.*;

class Fireball extends Entity {
    //Variables
    private final int SPEED = Integer.parseInt(getGameProps().getProperty("gameObjects.fireball.shootSpeedY"));
    private final double RADIUS = Double.parseDouble(getGameProps().getProperty("gameObjects.fireball.radius"));
    private final double DAMAGE = Double.parseDouble(getGameProps().getProperty("gameObjects.fireball.damage")) * 100;
    //Flag
    private boolean active = true;
    /**
     * Constructs a Fireball object at the specified coordinates with the given image.
     * The fireball moves upwards and deals damage to entities it collides with.
     *
     * @param x the x-coordinate of the fireball
     * @param y the y-coordinate of the fireball
     * @param image the image representing the fireball
     */
    public Fireball(double x, double y, Image image) {
        super(x, y, image);
    }
    /**
     * Checks if the fireball is currently active.
     * A fireball becomes inactive when it moves off-screen or collides with an entity.
     *
     * @return true if the fireball is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the fireball's active state.
     * A fireball can be deactivated when it moves off-screen or after a collision.
     *
     * @param active true if the fireball is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Gets the radius of the fireball, used for collision detection.
     *
     * @return the radius of the fireball
     */
    public double getRadius() {
        return RADIUS;
    }
    /**
     * Gets the damage that the fireball can deal to entities it collides with.
     *
     * @return the damage value of the fireball
     */
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Updates the position of the fireball, moving it upwards by its speed.
     * If the fireball moves off-screen, it becomes inactive.
     *
     * @param input the user input (though not used for the fireball's movement)
     */
    @Override
    public void update(Input input) {
        setY(getY() - SPEED);  // Move upwards
        if (getY() + RADIUS < 0) {  // If fireball is off-screen
            active = false;
        }
    }

    /**
     * Draws the fireball on the screen if it is active.
     * The fireball will not be rendered if it is inactive.
     */
    @Override
    public void draw() {
        if (active) {
            super.draw();
        }
    }
}