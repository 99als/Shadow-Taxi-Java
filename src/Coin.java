import bagel.*;
class Coin extends Entity {
    //Coin dimensions
    private final int COIN_MOVE_SPEED = Integer.parseInt(getGameProps().getProperty("gameObjects.taxi.speedY"));
    private final double COIN_RADIUS = Double.parseDouble(getGameProps().getProperty("gameObjects.coin.radius"));
    private final int COIN_MAX_FRAMES = Integer.parseInt(getGameProps().getProperty("gameObjects.coin.maxFrames"));

    //Coin Flag
    private boolean coinCollided = false;
    /**
     * Constructs a Coin object with the specified coordinates and image.
     *
     * @param x the x-coordinate of the coin
     * @param y the y-coordinate of the coin
     * @param image the image representing the coin
     */
    public Coin(double x, double y, Image image) {
        super(x, y, image);
    }
    /**
     * Gets the radius of the coin, used for collision detection.
     *
     * @return the radius of the coin
     */
    public double getCOIN_RADIUS() {
        return COIN_RADIUS;
    }
    /**
     * Checks if the coin has collided with another entity.
     *
     * @return true if the coin has collided, false otherwise
     */
    public boolean isCoinCollided() {
        return coinCollided;
    }
    /**
     * Sets the coin's collision state.
     *
     * @param coinCollided true if the coin has collided, false otherwise
     */
    public void setCoinCollided(boolean coinCollided) {
        this.coinCollided = coinCollided;
    }

    /**
     * Updates the state of the coin based on user input.
     * Moves the coin downward when the UP key is pressed.
     *
     * @param input the user input for controlling the coin
     */
    @Override
    public void update(Input input) {
        // Move the coin down when UP is inputted
        if (input.isDown(Keys.UP)) {
            setY(getY() + COIN_MOVE_SPEED);
        }
    }

    /**
     * Draws the coin on the screen if it hasn't collided.
     * If the coin has collided, it will not be rendered.
     */
    @Override
    public void draw() {
        if (coinCollided) {
            return;
        }
        getImage().draw(getX(), getY());
    }
}