import bagel.*;
class EnemyCar extends Entity {
    //Variables
    private final double RADIUS = Double.parseDouble(getGameProps().getProperty("gameObjects.enemyCar.radius"));
    private double INITIAL_HEALTH = Double.parseDouble(getGameProps().getProperty("gameObjects.enemyCar.health"));
    private final double DAMAGE = Double.parseDouble(getGameProps().getProperty("gameObjects.enemyCar.damage")) * 100;
    private int speed;
    private final int FIREBALL_SPAWN_RATE = 300;
    private int effectFramesDuration = 0;
    private final int EFFECT_DURATION = Integer.parseInt(getGameProps().getProperty("gameObjects.smoke.ttl"));
    //Images
    private Image smokeImage = new Image("res/smoke.png");
    private Image fireImage = new Image("res/fire.png");
    //Flag
    private boolean shouldRender = true;
    private boolean isColliding = false;
    private boolean isDestroyed = false;
    private boolean shouldRenderSmoke = false;
    private boolean shouldRenderFire = false;

    /**
     * Constructs an EnemyCar object at the specified coordinates with the given image.
     * The speed of the car is set randomly between 2 and 5.
     *
     * @param x the x-coordinate of the enemy car
     * @param y the y-coordinate of the enemy car
     * @param image the image representing the enemy car
     */
    public EnemyCar(double x, double y, Image image) {
        super(x, y, image);
        this.speed = MiscUtils.getRandomInt(2, 5); // Random speed between 2 and 5
        this.health = INITIAL_HEALTH;
    }
    /**
     * Determines whether the enemy car should shoot a fireball.
     * This is based on a random chance determined by the FIREBALL_SPAWN_RATE.
     *
     * @return true if the car should shoot a fireball, false otherwise
     */
    public boolean shouldShootFireball() {
        return MiscUtils.canSpawn(FIREBALL_SPAWN_RATE);
    }
    /**
     * Gets the amount of damage the enemy car can deal.
     *
     * @return the damage value of the enemy car
     */
    public double getDamage(){
        return DAMAGE;
    }
    /**
     * Checks if the enemy car is destroyed.
     *
     * @return true if the car is destroyed, false otherwise
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }
    /**
     * Checks if the enemy car is currently colliding with another entity.
     *
     * @return true if the car is colliding, false otherwise
     */
    public boolean isColliding() {
        return isColliding;
    }
    /**
     * Gets the radius of the enemy car, used for collision detection.
     *
     * @return the radius of the car
     */
    public double getRadius() {
        return RADIUS;
    }
    /**
     * Applies damage to the enemy car, reducing its health.
     * If the car's health drops to 0 or below, it is marked as destroyed and no longer rendered.
     *
     * @param amount the amount of damage to apply
     */
    public void damage(double amount) {
        this.health -= amount;
        if (this.health <= 0) {
            shouldRenderFire = true;
            effectFramesDuration = EFFECT_DURATION;
            shouldRender = false;
            isDestroyed = true;
        } else {
            shouldRenderSmoke = true;
            effectFramesDuration = EFFECT_DURATION;
        }
    }
    /**
     * Updates the position of the enemy car, moving it upwards by its speed.
     *
     * @param input the user input, though not used for the enemy car
     */
    @Override
    public void update(Input input) {
        setY(getY() - speed); // Move upwards

        if (effectFramesDuration > 0) {
            effectFramesDuration--;
        } else {
            shouldRenderSmoke = false;
            shouldRenderFire = false;
        }
    }
    /**
     * Draws the enemy car on the screen if it hasn't been destroyed.
     * If the car is destroyed, it is no longer rendered.
     */
    @Override
    public void draw(){
        if (shouldRenderSmoke && effectFramesDuration > 0) {
            smokeImage.draw(getX(), getY());
        } else if (shouldRenderFire && effectFramesDuration > 0) {
            fireImage.draw(getX(), getY());
        }
        if (shouldRender) {
            super.draw();
        }
    }

}