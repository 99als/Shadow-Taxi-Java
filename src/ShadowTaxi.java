import bagel.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2024
 * Please enter your name below
 * Min-Hyuk Choi
 */

public class ShadowTaxi extends AbstractGame {
    //Game/Message properties file
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    //Coordinates
    private final int WINDOW_WIDTH;
    private final int WINDOW_HEIGHT;
    private final int HOME_TITLE_Y;
    private final int HOME_INSTRUCTION_Y;
    private final int PLAYER_INFO_START_Y;
    private final int PLAYER_INFO_NAME_INPUT_Y;
    private final int PLAYER_INFO_NAME_Y;
    private final int GAME_END_SCORE_Y;
    private final int COIN_X;
    private final int COIN_Y;

    //Images for backgrounds/game objects
    private final Image HOME_BACKGROUND_IMAGE;
    private final Image PLAYER_INFO_BACKGROUND_IMAGE;
    private final Image GAME_BACKGROUND_IMAGE;
    private final Image END_BACKGROUND_IMAGE;
    private final Image TAXI;
    private final Image COIN;
    private final Image PASSENGER;
    private final Image DRIVER;
    private final Image POWERUP;
    private final Image TRIPENDFLAG;
    private final Image ENEMYCAR;
    private final Image CAR_TYPE_ONE;
    private final Image CAR_TYPE_TWO;
    private final Image DAMAGEDTAXI;
    private final Image FIREBALL;

    //Fonts
    private final Font TITLE_FONT;
    private final Font INSTRUCTION_FONT;
    private final Font PLAYER_INFO_FONT;
    private final Font SCORE_FONT;
    private final Font EARNINGS_FONT;
    private final Font END_MESSAGE_FONT;

    //Strings
    private final String HOME_TITLE;
    private final String HOME_INSTRUCTION;
    private final String PLAYER_INFO_START;
    private final String PLAYER_NAME;
    private final String PAY_EARNINGS;
    private final String FRAMES_REM;
    private final String TARGET;
    private final String LAST_TRIP;
    private final String CURRENT_TRIP;
    private final String EXPECTED_FEE;
    private final String PRIORITY;
    private final String PENALTY;
    private final String GAME_LOST;
    private final String GAME_WON;
    private final String TOP_5_SCORES;

    //Flags for game state
    private boolean gameStarted;
    private boolean playerInfoScreen;
    private boolean gamePlayScreen;
    private boolean firstPassengerPickedUp;
    private double lastTripExpectedEarnings;
    private int lastTripPriority;
    private boolean loseScreen;
    private boolean scoreSubmitted;

    //Frames
    private int MAX_FRAMES;
    private int COIN_FRAMES;
    private int MAX_COIN_FRAMES;
    private double SCORE_GOAL;

    // String to store the player's name
    private String playerName;

    //Background for the scrolling effect
    private double backgroundY1;
    private double backgroundY2;

    //Weather
    private List<WeatherCondition> weatherConditions;
    private int currentFrame;
    private Image sunnyBackground;
    private Image rainyBackground;

    //Score variables
    private double totalScore;
    private double penaltyRate;
    private double lastTripPenalty;
    private boolean tripInProgress;

    //Coords
    private double TRIPINFOCOORDX;
    private double TRIPINFOCOORDY;
    private double MAXFRAMESX;
    private double MAXFRAMESY;
    private double TARGETX;
    private double TARGETY;
    private double EARNINGSX;
    private double EARNINGSY;
    private int GAMEENDSCORE;
    private int LEFTLANE;
    private int MIDDLELANE;
    private int RIGHTLANE;
    private int PASSENGER_HEALTH_X;
    private int PASSENGER_HEALTH_Y;
    private int TAXI_HEALTH_X;
    private int TAXI_HEALTH_Y;
    private int DRIVER_HEALTH_X;
    private int DRIVER_HEALTH_Y;
    private int PASSENGER_RADIUS;
    private int PASSENGER_DETECT_TAXI_RADIUS;
    private int TRIP_END_FLAG_RADIUS;

    //Speed
    private final double SCROLL_SPEED;

    //Y pos to reset background
    private final static double RESET_POSITION = 1152; // Position to reset the background (no magic numbers)

    //Entity list
    private List<Entity> entities; // List to store game entities
    private List<Fireball> fireballs = new ArrayList<>();

    //Spawn Related
    private int CAR_SPAWN_RATE;
    private int ENEMY_CAR_SPAWN_RATE;
    private int TAXI_SPAWN_MIN_Y;
    private int TAXI_LOWER_Y;
    private int TAXI_UPPER_Y;
    private Taxi nextTaxi;

    //Damage
    private double TAXI_DAMAGE;
    private double FIREBALL_DAMAGE;
    private double CAR_DAMAGE;
    private double ENEMY_CAR_DAMAGE;
    private int COLLISION_TIMEOUT;
    private int RENDER_DURATION;
    private int COLLISION_SPEED;
    private int bloodRenderCounter;
    private final int BLOOD_RENDER_DURATION;

    public ShadowTaxi(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        //Load the background images
        HOME_BACKGROUND_IMAGE = new Image(gameProps.getProperty("backgroundImage.home"));
        PLAYER_INFO_BACKGROUND_IMAGE = new Image(gameProps.getProperty("backgroundImage.playerInfo"));
        GAME_BACKGROUND_IMAGE = new Image(gameProps.getProperty("backgroundImage.raining"));
        END_BACKGROUND_IMAGE = new Image(gameProps.getProperty("backgroundImage.gameEnd"));

        //Scroll Speed
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));

        //Initialise game properties (Generally Co-ordinates)
        WINDOW_WIDTH = Integer.parseInt(gameProps.getProperty("window.width"));
        WINDOW_HEIGHT = Integer.parseInt(gameProps.getProperty("window.height"));
        HOME_TITLE_Y = Integer.parseInt(gameProps.getProperty("home.title.y"));
        HOME_INSTRUCTION_Y = Integer.parseInt(gameProps.getProperty("home.instruction.y"));
        PLAYER_INFO_START_Y = Integer.parseInt(gameProps.getProperty("playerInfo.start.y"));
        PLAYER_INFO_NAME_INPUT_Y = Integer.parseInt(gameProps.getProperty("playerInfo.playerNameInput.y"));
        PLAYER_INFO_NAME_Y = Integer.parseInt(gameProps.getProperty("playerInfo.playerName.y"));
        GAME_END_SCORE_Y = Integer.parseInt(gameProps.getProperty("gameEnd.status.y"));
        TRIPINFOCOORDX = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.tripInfo.x"));
        TRIPINFOCOORDY = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.tripInfo.y"));
        MAXFRAMESX = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames.x"));
        MAXFRAMESY = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames.y"));
        TARGETX = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.target.x"));
        TARGETY = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.target.y"));
        EARNINGSX = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.earnings.x"));
        EARNINGSY = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.earnings.y"));
        GAMEENDSCORE = Integer.parseInt(GAME_PROPS.getProperty("gameEnd.scores.y"));
        LEFTLANE = Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter1"));
        MIDDLELANE = Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter2"));
        RIGHTLANE = Integer.parseInt(GAME_PROPS.getProperty("roadLaneCenter3"));
        PASSENGER_RADIUS = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.passenger.radius"));
        TRIP_END_FLAG_RADIUS = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.tripEndFlag.radius"));
        COIN_X = Integer.parseInt(GAME_PROPS.getProperty("gameplay.coin.x"));
        COIN_Y = Integer.parseInt(GAME_PROPS.getProperty("gameplay.coin.y"));


        //Messages
        HOME_TITLE = MESSAGE_PROPS.getProperty("home.title");
        HOME_INSTRUCTION = MESSAGE_PROPS.getProperty("home.instruction");
        PLAYER_INFO_START = MESSAGE_PROPS.getProperty("playerInfo.start");
        PLAYER_NAME = MESSAGE_PROPS.getProperty("playerInfo.playerName");
        PAY_EARNINGS = MESSAGE_PROPS.getProperty("gamePlay.earnings");
        FRAMES_REM = MESSAGE_PROPS.getProperty("gamePlay.remFrames");
        TARGET = MESSAGE_PROPS.getProperty("gamePlay.target");
        LAST_TRIP = MESSAGE_PROPS.getProperty("gamePlay.completedTrip.title");
        CURRENT_TRIP = MESSAGE_PROPS.getProperty("gamePlay.onGoingTrip.title");
        EXPECTED_FEE = MESSAGE_PROPS.getProperty("gamePlay.trip.expectedEarning");
        PRIORITY = MESSAGE_PROPS.getProperty("gamePlay.trip.priority");
        PENALTY = MESSAGE_PROPS.getProperty("gamePlay.trip.penalty");
        GAME_LOST = MESSAGE_PROPS.getProperty("gameEnd.lost");
        GAME_WON = MESSAGE_PROPS.getProperty("gameEnd.won");
        TOP_5_SCORES = MESSAGE_PROPS.getProperty("gameEnd.highestScores");

        //Load entity images
        TAXI = new Image("res/taxi.png");
        COIN = new Image("res/coin.png");
        PASSENGER = new Image("res/passenger.png");
        TRIPENDFLAG = new Image("res/tripEndFlag.png");
        DRIVER = new Image("res/driver.png");
        POWERUP = new Image("res/invinciblePower.png");
        ENEMYCAR = new Image("res/enemyCar.png");
        CAR_TYPE_ONE = new Image("res/otherCar-1.png");
        CAR_TYPE_TWO = new Image("res/otherCar-2.png");
        DAMAGEDTAXI = new Image("res/taxiDamaged.png");
        FIREBALL = new Image("res/fireball.png");

        //Create the font variables
        TITLE_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("home.title.fontSize")));
        INSTRUCTION_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("home.instruction.fontSize")));
        PLAYER_INFO_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("playerInfo.fontSize")));
        SCORE_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("gameEnd.scores.fontSize")));
        EARNINGS_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("gameObjects.passenger.fontSize")));
        END_MESSAGE_FONT = new Font(gameProps.getProperty("font"), Integer.parseInt(gameProps.getProperty("gameEnd.status.fontSize")));

        //Initial flag states
        gameStarted = false;
        playerInfoScreen = false;
        gamePlayScreen = false;
        playerName = "";
        backgroundY1 = HOME_TITLE_Y;
        backgroundY2 = -HOME_TITLE_Y;
        totalScore = 0;
        lastTripPenalty = 0;
        tripInProgress = false;
        firstPassengerPickedUp = false;
        lastTripExpectedEarnings = 0;
        lastTripPriority = 0;
        loseScreen = false;
        scoreSubmitted = false;

        //Scores
        MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
        SCORE_GOAL = Double.parseDouble(gameProps.getProperty("gamePlay.target"));
        COIN_FRAMES = Integer.parseInt(gameProps.getProperty("gameObjects.coin.maxFrames"));
        MAX_COIN_FRAMES = Integer.parseInt(gameProps.getProperty("gameObjects.coin.maxFrames"));
        penaltyRate = Double.parseDouble(gameProps.getProperty("trip.penalty.perY"));
        PASSENGER_HEALTH_X = Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.x"));
        PASSENGER_HEALTH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.passengerHealth.y"));
        TAXI_HEALTH_X = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.x"));
        TAXI_HEALTH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.y"));
        DRIVER_HEALTH_X= Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.x"));
        DRIVER_HEALTH_Y = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.y"));

        //Initialise entity list
        entities = new ArrayList<>();

        //Weather
        weatherConditions = readWeatherData();
        currentFrame = 0;
        sunnyBackground = new Image(gameProps.getProperty("backgroundImage.sunny"));
        rainyBackground = new Image(gameProps.getProperty("backgroundImage.raining"));

        //Spawns
        CAR_SPAWN_RATE = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY"));
        ENEMY_CAR_SPAWN_RATE = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"));
        TAXI_SPAWN_MIN_Y = -50;
        TAXI_LOWER_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY"));
        TAXI_UPPER_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"));
        PASSENGER_DETECT_TAXI_RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.taxiDetectRadius"));
        Taxi nextTaxi;

        //Entity dmg/timeout
        TAXI_DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")) * 100;
        FIREBALL_DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damage")) * 100;
        CAR_DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.damage")) * 100;
        ENEMY_CAR_DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.damage")) * 100;
        COLLISION_TIMEOUT = 200;
        RENDER_DURATION = Integer.parseInt(gameProps.getProperty("gameObjects.smoke.ttl"));
        COLLISION_SPEED = 1;
        BLOOD_RENDER_DURATION = Integer.parseInt(gameProps.getProperty("gameObjects.smoke.ttl"));
        bloodRenderCounter = 0;

        // Load entities from the world file
        loadEntities();

    }

    /**
     * Render the relevant screens and game objects based on the keyboard input
     * given by the user and the status of the game play.
     *
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void
    update(Input input) {
        // Close the window if the escape key is pressed.
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        // If game hasn't started, draw the home screen.
        if (!gameStarted) {
            drawHomeScreen(input);
        }
        // Draw the player info screen if the player info screen is active
        else if (playerInfoScreen) {
            drawPlayerInfoScreen(input);
        }
        // Draw the gameplay screen if the gameplay screen is active
        else if (gamePlayScreen) {
            drawGamePlayScreen(input);
            determineAction(input);
            drawScore();
            drawHealthInfo();
            decreaseMaxFrames();
            drawCoinFrames();
            increaseCoinFrames();
            generateRandomCars();
            checkCarCollisions();
            updateFireballs(input);
            generateFireballs();
            checkFireballCollisions();
            checkAndCreateNewTaxi();
            checkDriverTaxiInteraction();
        } else {
            displayEndScreen(input);
        }
    }

    /**
     * Draws the home screen, displaying the title and instructions for starting the game.
     *
     * @param input The current input from the keyboard.
     */
    public void drawHomeScreen(Input input) {
        //Center the image, dividing by 2
        HOME_BACKGROUND_IMAGE.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);
        //Draw game title centered horizontally at set Y co-ordinate
        TITLE_FONT.drawString(HOME_TITLE,
                (WINDOW_WIDTH - TITLE_FONT.getWidth(HOME_TITLE)) / 2.0, HOME_TITLE_Y);

        //Draw instruction at set Y co-ordinate
        INSTRUCTION_FONT.drawString(HOME_INSTRUCTION,
                (WINDOW_WIDTH - INSTRUCTION_FONT.getWidth(HOME_INSTRUCTION)) / 2.0, HOME_INSTRUCTION_Y);

        //If enter pressed move to next screen
        if (input.wasPressed(Keys.ENTER)) {
            gameStarted = true;
            playerInfoScreen = true;
        }
    }

    /**
     * Draws the player information screen where the player can input their name.
     * This method handles the rendering of the player input screen, captures keyboard input for name entry,
     * and transitions to the gameplay screen once the player presses the Enter key.
     *
     * @param input The current input from the player (keyboard).
     */
    public void drawPlayerInfoScreen(Input input) {
        //Center the image, dividing by 2
        PLAYER_INFO_BACKGROUND_IMAGE.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

        // Draw the "Enter your name"
        String instruction = PLAYER_NAME;
        PLAYER_INFO_FONT.drawString(instruction,
                (WINDOW_WIDTH - PLAYER_INFO_FONT.getWidth(instruction)) / 2.0, PLAYER_INFO_NAME_Y);

        // Set text color to black and draw the player's name
        DrawOptions drawOptions = new DrawOptions();
        drawOptions.setBlendColour(0.0, 0.0, 0.0);
        PLAYER_INFO_FONT.drawString(playerName,
                (WINDOW_WIDTH - PLAYER_INFO_FONT.getWidth(playerName)) / 2.0, PLAYER_INFO_NAME_INPUT_Y, drawOptions);

        // Vertically space out the lines of text
        String[] lines = PLAYER_INFO_START.split("\n");
        for (int i = 0; i < lines.length; i++) {
            PLAYER_INFO_FONT.drawString(lines[i],
                    (WINDOW_WIDTH - PLAYER_INFO_FONT.getWidth(lines[i])) / 2.0, PLAYER_INFO_START_Y + i * 30);
        }

        // Remove the last character if Backspace is pressed
        if ((input.wasPressed(Keys.BACKSPACE) || (input.wasPressed(Keys.DELETE))) && !playerName.isEmpty()) {
            playerName = playerName.substring(0, playerName.length() - 1);

            // Press enter to start the game
        } else if (input.wasPressed(Keys.ENTER)) {
            playerInfoScreen = false;
            gamePlayScreen = true;
        } else {
            //Append the character to the name
            String keyPressed = MiscUtils.getKeyPress(input);
            if (keyPressed != null && !keyPressed.isEmpty()) {
                playerName += keyPressed;
            }
        }
    }

    /**
     * Draws the gameplay screen and handles updates to game entities.
     *
     * @param input The current input from the keyboard.
     */
    public void drawGamePlayScreen(Input input) {
        //Get the weather
        String currentWeather = getCurrentWeather();
        Image currentBackground = currentWeather.equals("SUNNY") ? sunnyBackground : rainyBackground;

        // Draw the backgrounds
        currentBackground.draw(WINDOW_WIDTH / 2.0, backgroundY1);
        currentBackground.draw(WINDOW_WIDTH / 2.0, backgroundY2);

        // Move the backgrounds down when the up arrow key is pressed
        if (input.isDown(Keys.UP)) {
            backgroundY1 += SCROLL_SPEED;
            backgroundY2 += SCROLL_SPEED;
        }

        // Reset the background to create the scrolling effect
        if (backgroundY1 >= RESET_POSITION) {
            backgroundY1 = backgroundY2 - WINDOW_HEIGHT;
        }
        if (backgroundY2 >= RESET_POSITION) {
            backgroundY2 = backgroundY1 - WINDOW_HEIGHT;
        }

        // Update and draw all entities from list
        for (Entity entity : entities) {
            if (entity instanceof Passenger passenger && !((Passenger) entity).isEjected) {
                passenger.adjustPriorityForWeather(currentWeather);
                displayPassengerEarnings(passenger);
            }
            if (entity instanceof Coin) {
                checkCoinCollision((Coin) entity);
            }
            if (entity instanceof InvinciblePower){
                checkPowerUpCollision((InvinciblePower) entity);
            }
            entity.update(input);
            entity.draw();
        }
        for (Fireball fireball : fireballs) {
            fireball.draw();
        }
        checkLossConditions();
        displayTripDetails();
        checkGameCondition();
        currentFrame++;
    }
    /**
     * Checks the game conditions for winning or losing.
     * If the player's score meets or exceeds the target score, the game ends as a win.
     * If the frames count reaches zero, the game ends as a loss.
     */
    private void checkGameCondition() {
        //Win
        if (totalScore >= SCORE_GOAL) {
            gamePlayScreen = false;
            playerInfoScreen = false;
            return;
        }
        //Loss
        if (MAX_FRAMES <= 0) {
            loseScreen = true;
            gamePlayScreen = false;
        }
    }

    private void checkLossConditions() {
        Driver driver = null;
        Passenger passenger = null;

        // Iterate through all entities to check both taxi loss and health loss conditions
        for (Entity entity : entities) {
            if (entity instanceof Taxi) {
                Taxi taxi = (Taxi) entity;

                // Check if the taxi doesn't have a driver and its y-coordinate exceeds the window height
                if (taxi.getDriver() == null && taxi.getY() >= WINDOW_HEIGHT) {
                    // Trigger game loss for taxi condition
                    gamePlayScreen = false;
                    loseScreen = true;
                    return;
                }
            }

            // Find the driver and passenger entities
            if (entity instanceof Driver) {
                driver = (Driver) entity;
            } else if (entity instanceof Passenger && ((Passenger) entity).wasOnTripWhenEjected()) {
                passenger = (Passenger) entity;
            }
        }

        // Check if the driver's or passenger's health is 0 or less
        if ((driver != null && driver.getHealth() <= 0) || (passenger != null && passenger.getHealth() <= 0)) {
            if (bloodRenderCounter < BLOOD_RENDER_DURATION) {
                bloodRenderCounter++;
            } else {
                // Trigger game loss for health condition
                loseScreen = true;
                gamePlayScreen = false;
            }

        } else {
            bloodRenderCounter = 0;
        }
    }


    private void displayEndScreen(Input input) {
        String[][] LeaderBoard = IOUtils.readCommaSeparatedFile("res/scores.csv");

        // If the score hasn't been submitted yet, add it to the leaderboard
        if (!scoreSubmitted) {
            String[] newEntry = {playerName, String.format("%.2f", totalScore)};
            LeaderBoard = addEntryToLeaderBoard(LeaderBoard, newEntry);
            scoreSubmitted = true; // Mark score as submitted to avoid adding it again
        }

        // Sorting in descending order
        Arrays.sort(LeaderBoard, new Comparator<String[]>() {
            @Override
            public int compare(String[] entry1, String[] entry2) {
                double score1 = Double.parseDouble(entry1[1]);
                double score2 = Double.parseDouble(entry2[1]);
                return Double.compare(score2, score1);
            }
        });

        // Keep only top 5 or if not 5, print all
        LeaderBoard = Arrays.copyOfRange(LeaderBoard, 0, Math.min(5, LeaderBoard.length));

        // Clear the original CSV file and write the top 5 scores back
        try (PrintWriter pw = new PrintWriter("res/scores.csv")) {
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the top 5 scores back to the file
        for (String[] entry : LeaderBoard) {
            String line = String.join(",", entry);
            IOUtils.writeScoreToFile("res/scores.csv", line);
        }

        // Render the background
        END_BACKGROUND_IMAGE.draw(WINDOW_WIDTH / 2.0, WINDOW_HEIGHT / 2.0);

        // Display the TOP 5 SCORES title at the top
        SCORE_FONT.drawString(TOP_5_SCORES,
                (WINDOW_WIDTH - SCORE_FONT.getWidth(TOP_5_SCORES)) / 2.0,
                GAMEENDSCORE);

        // Display each score entry, starting from y = 240
        double scoreY = 240;
        for (String[] strings : LeaderBoard) {
            String entry = String.format("%s - %.2f", strings[0], Double.parseDouble(strings[1]));
            SCORE_FONT.drawString(entry,
                    (WINDOW_WIDTH - SCORE_FONT.getWidth(entry)) / 2.0,
                    scoreY);
            scoreY += 40; // Increase the y-coordinate for the next score
        }

        // Choose end message
        String endMessage;
        if (loseScreen) {
            endMessage = GAME_LOST;
        } else {
            endMessage = GAME_WON;
        }
        String[] endMessages = endMessage.split("\n");
        endMessage = endMessages[0];
        String endMessage2 = endMessages[1];
        END_MESSAGE_FONT.drawString(endMessage,
                (WINDOW_WIDTH - END_MESSAGE_FONT.getWidth(endMessage)) / 2.0,
                GAME_END_SCORE_Y);

        // Render the "Press space to continue" message below the end message, account for newline
        END_MESSAGE_FONT.drawString(endMessage2,
                (WINDOW_WIDTH - END_MESSAGE_FONT.getWidth("PRESS SPACE TO CONTINUE")) / 2.0,
                GAME_END_SCORE_Y + 40);
        // Press space to reset the game
        if (input.wasPressed(Keys.SPACE)) {
            resetGame();
        }
    }


    /**
     * Adds a new entry to the leaderboard.
     *
     * This method creates a new leaderboard array with an additional space for the new entry.
     * It copies the existing leaderboard entries to the new array and appends the new entry at the end.
     *
     * @param leaderboard The current leaderboard represented as a 2D array of strings, where each entry is an array
     *                    containing the player's name and score.
     * @param newEntry    A string array representing the new entry, where the first element is the player's name
     *                    and the second element is the player's score.
     * @return A new 2D array containing all previous leaderboard entries along with the new entry.
     */
    public String[][] addEntryToLeaderBoard(String[][] leaderboard, String[] newEntry) {
        String[][] updatedLeaderboard = new String[leaderboard.length + 1][];
        for (int i = 0; i < leaderboard.length; i++) {
            updatedLeaderboard[i] = leaderboard[i];
        }
        updatedLeaderboard[leaderboard.length] = newEntry;
        return updatedLeaderboard;
    }

    //Player should play again if Space is pressed
    private void resetGame() {
        // Reset the game-related variables
        totalScore = 0;
        lastTripPenalty = 0;
        tripInProgress = false;
        firstPassengerPickedUp = false;
        lastTripExpectedEarnings = 0;
        lastTripPriority = 0;
        MAX_FRAMES = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames"));
        COIN_FRAMES = Integer.parseInt(GAME_PROPS.getProperty("gameObjects.coin.maxFrames"));

        // Clear the list of game entities and reload them
        entities.clear();
        loadEntities();

        // Reset flags for win/lose screen and game state
        loseScreen = false;
        gameStarted = false;
        gamePlayScreen = false;

        playerName = ""; // This resets the name
        scoreSubmitted = false;
        currentFrame = 0;
    }
    /**
     * Represents a weather condition in the game, such as "SUNNY" or "RAINY".
     * A weather condition has a specific time frame during which it is active.
     */
    public class WeatherCondition {
        String condition;
        int startFrame;
        int endFrame;
        /**
         * Constructs a new WeatherCondition object.
         *
         * @param condition The type of weather (e.g., "SUNNY", "RAINY").
         * @param startFrame The frame at which the weather condition starts.
         * @param endFrame The frame at which the weather condition ends.
         */
        public WeatherCondition(String condition, int startFrame, int endFrame) {
            this.condition = condition;
            this.startFrame = startFrame;
            this.endFrame = endFrame;
        }
    }
    //Iterates through all the weather conditions, changing them based on the frames.
    private String getCurrentWeather() {
        for (WeatherCondition condition : weatherConditions) {
            if (currentFrame >= condition.startFrame && currentFrame < condition.endFrame) {
                return condition.condition;
            }
        }
        return "SUNNY"; // Default to sunny if no condition matches
    }
    /**
     * Reads weather data from the gameWeather.csv file and converts it into a list of weather conditions.
     * <p>
     * This method reads the weather conditions from a CSV file, where each row contains the weather type,
     * the start frame, and the end frame for that condition. The data is parsed and stored as a list of
     * {WeatherCondition} objects.
     *
     * @return A list of {WeatherCondition} objects representing the weather conditions from the CSV file.
     */
    public List<WeatherCondition> readWeatherData() {
        List<WeatherCondition> weatherConditions = new ArrayList<>();
        String[][] data = IOUtils.readCommaSeparatedFile("res/gameWeather.csv");

        for (String[] row : data) {
            String condition = row[0];
            int startFrame = Integer.parseInt(row[1]);
            int endFrame = Integer.parseInt(row[2]);
            weatherConditions.add(new WeatherCondition(condition, startFrame, endFrame));
        }

        return weatherConditions;
    }

    // Method to load entities from the world file
    private void loadEntities() {
        //2D array to store data from CSV file
        String[][] data = IOUtils.readCommaSeparatedFile("res/gameObjects.csv");

        Taxi firstTaxi = null;  // Store the first taxi created
        Driver firstDriver = null; //Only one driver in the game

        for (String[] row : data) {
            //First column is entity
            String type = row[0];
            //Coordinates
            double x = Double.parseDouble(row[1]);
            double y = Double.parseDouble(row[2]);
            //Add entities to the list depending on case
            switch (type) {
                case "TAXI":
                    Taxi taxi = new Taxi(x, y, TAXI, DAMAGEDTAXI);
                    entities.add(taxi);
                    firstTaxi = taxi;
                    break;
                case "COIN":
                    entities.add(new Coin(x, y, COIN));
                    break;
                case "PASSENGER":
                    int priority = Integer.parseInt(row[3]);
                    int endX = Integer.parseInt(row[4]);
                    int yDistance = Integer.parseInt(row[5]);
                    boolean hasUmbrella = (Integer.parseInt(row[6]) == 1);
                    entities.add(new Passenger(x, y, priority, endX, yDistance,hasUmbrella, PASSENGER));
                    break;
                case "DRIVER":
                    Driver driver = new Driver(x, y, DRIVER);
                    entities.add(driver);
                    firstDriver = driver;
                    break;
                case "INVINCIBLE_POWER":
                    entities.add(new InvinciblePower(x, y, POWERUP));
                    break;
            }
        }
        // If both the first taxi and driver were created, assign the driver to the taxi
        if (firstTaxi != null && firstDriver != null) {
            firstTaxi.setDriver(firstDriver);
            firstDriver.setCurrentTaxi(firstTaxi);
        }
    }

    private void generateRandomCars() {
        if (MiscUtils.canSpawn(CAR_SPAWN_RATE)) {
            createCar(false);
        }
        if (MiscUtils.canSpawn(ENEMY_CAR_SPAWN_RATE)) {
            createCar(true);
        }
    }

    private void createCar(boolean isEnemy) {
        // Randomly select a lane from one of the three lanes (360, 480, 620)
        int lane = MiscUtils.getRandomInt(0, 3); // Get random index for lanes
        int[] lanes = {LEFTLANE, MIDDLELANE, RIGHTLANE};
        int xCoord = lanes[lane]; // Choose the corresponding lane based on random index

        // Randomly select the y-coordinate (-50 or 768)
        int yCoord = MiscUtils.selectAValue(TAXI_SPAWN_MIN_Y, WINDOW_HEIGHT);

        // Load the appropriate image and create the car object
        Image carImage;
        if (isEnemy) {
            carImage = ENEMYCAR;
            entities.add(new EnemyCar(xCoord, yCoord, carImage));
        } else {
            int carType = MiscUtils.getRandomInt(1, 2);
            if (carType == 1) {
                carImage = CAR_TYPE_ONE;
            } else {
                carImage = CAR_TYPE_TWO;
            }
            entities.add(new Car(xCoord, yCoord, carImage));
        }
    }

    private void checkAndCreateNewTaxi() {
        //Flag for active taxi (!permanentlyDamaged)
        boolean activeTaxiExists = false;

        for (Entity entity : entities) {
            if (entity instanceof Taxi) {
                Taxi taxi = (Taxi) entity;
                //Found active taxi
                if (!taxi.isPermanentlyDamaged()) {
                    activeTaxiExists = true;
                    break;
                }
            }
        }
        //If no active taxi was found, create a new one
        if (!activeTaxiExists) {
            createNewTaxi();
        }
    }

    private void createNewTaxi() {
        int laneChoice = MiscUtils.getRandomInt(0, 2);
        int xCoordinate;
        int yCoordinate = MiscUtils.getRandomInt(TAXI_LOWER_Y, TAXI_UPPER_Y);

        if (laneChoice == 0) {
            xCoordinate = LEFTLANE;
        } else {
            xCoordinate = RIGHTLANE;
        }

        Taxi newTaxi = new Taxi(xCoordinate, yCoordinate, TAXI, DAMAGEDTAXI);
        entities.add(newTaxi);

    }

    private void checkDriverTaxiInteraction() {
        Driver driver = null;
        Taxi nearestTaxi = null;
        Passenger ejectedPassenger = null;

        for (Entity entity : entities) {
            if (entity instanceof Driver) {
                driver = (Driver) entity; // Identify the driver
            } else if (entity instanceof Taxi && !((Taxi) entity).isPermanentlyDamaged() && ((Taxi) entity).getDriver() == null) {
                // Since only one active taxi at a time, assign it directly
                nearestTaxi = (Taxi) entity;
            } else if (entity instanceof Passenger && ((Passenger) entity).isEjected) {
                ejectedPassenger = (Passenger) entity;
            }
        }

        // If a driver and the taxi were found, and the driver can enter the taxi, enter
        if (driver != null && nearestTaxi != null && !driver.isInTaxi() && driver.canEnterTaxi(nearestTaxi)) {
            driver.enterTaxi(nearestTaxi); // The driver enters the taxi
            if (ejectedPassenger != null) {
                ejectedPassenger.isEjected = false;
            }
        }
    }

    private void updateFireballs(Input input) {
        for (Fireball fireball : fireballs) {
            fireball.update(input);
        }
        fireballs.removeIf(fireball -> !fireball.isActive());
    }

    private void generateFireballs() {
        for (Entity entity : entities) {
            if (entity instanceof EnemyCar) {
                EnemyCar enemyCar = (EnemyCar) entity;
                //If enemy car is not destroyed, should be able to shoot fireballs
                if (!enemyCar.isDestroyed() && enemyCar.shouldShootFireball()) {
                    fireballs.add(new Fireball(enemyCar.getX(), enemyCar.getY(), FIREBALL));
                }
            }
        }
    }

    private void checkFireballCollisions() {
        for (Fireball fireball : fireballs) {
            for (Entity entity : entities) {
                if (entity instanceof Taxi || entity instanceof Driver || entity instanceof Passenger || entity instanceof Car) {

                    // Get the entity's radius based on its type
                    double entityRadius = (entity instanceof Taxi) ? ((Taxi) entity).getTaxiRadius() :
                            (entity instanceof Car) ? ((Car) entity).getRadius() :
                                    (entity instanceof Driver) ? ((Driver) entity).getRadius() :
                                            PASSENGER_RADIUS;

                    // Check if the fireball collides with the entity
                    if (calculateEuclideanDistance(fireball.getX(), fireball.getY(),
                            entity.getX(), entity.getY()) <= fireball.getRadius() + entityRadius) {

                        // Apply damage
                        entity.damage(fireball.getDamage());

                        if (entity instanceof Driver && !((Driver) entity).isInvincible()) {
                            ((Driver) entity).damage(fireball.getDamage());
                        } else if (entity instanceof Passenger) {
                            ((Passenger) entity).damage(fireball.getDamage());
                        } else if (entity instanceof Car) {
                            ((Car) entity).damage(fireball.getDamage());
                        }

                        // Deactivate the fireball after the collision
                        fireball.setActive(false);
                        break;
                    }
                }
            }
        }
    }

    private void checkCarCollisions() {
        for (Entity entity1 : entities) {
            for (Entity entity2 : entities) {
                if (entity1 != entity2) {
                    if (checkCollision(entity1, entity2)) {
                        handleCollision(entity1, entity2);
                    }
                }
            }
        }
    }

    private boolean checkCollision(Entity entity1, Entity entity2) {
        //Should phase through permanently damaged object
        if ((entity1 instanceof Taxi && ((Taxi) entity1).isPermanentlyDamaged()) ||
                (entity2 instanceof Taxi && ((Taxi) entity2).isPermanentlyDamaged())) {
            return false;
        }

        double distance = calculateEuclideanDistance(entity1.getX(), entity1.getY(), entity2.getX(), entity2.getY());
        double collisionRadius = getCollisionRadius(entity1) + getCollisionRadius(entity2);
        return distance <= collisionRadius;
    }
    //Depending on the type of entity, return the collision radius
    private double getCollisionRadius(Entity entity) {
        if (entity instanceof Taxi) return ((Taxi) entity).getTaxiRadius();
        if (entity instanceof Car) return ((Car) entity).getRadius();
        if (entity instanceof Driver) return ((Driver) entity).getRadius();
        if (entity instanceof EnemyCar) return ((EnemyCar) entity).getRadius();
        if (entity instanceof Passenger) return PASSENGER_RADIUS;
        return 0;
    }
    //Use-cases for all types of entity collision
    private void handleCollision(Entity entity1, Entity entity2) {
        if (entity1 instanceof Taxi && (entity2 instanceof Car || entity2 instanceof EnemyCar)) {
            handleTaxiVehicleCollision((Taxi) entity1, entity2);
        } else if (entity1 instanceof Driver && !((Driver) entity1).isInTaxi() &&
                (entity2 instanceof Car || entity2 instanceof EnemyCar)) {
            handleDriverVehicleCollision((Driver) entity1, entity2);
        } else if (entity1 instanceof Passenger && !((Passenger) entity1).isInTaxi() &&
                (entity2 instanceof Car || entity2 instanceof EnemyCar)) {
            handlePassengerVehicleCollision((Passenger) entity1, entity2);
        } else if ((entity1 instanceof Car || entity1 instanceof EnemyCar) &&
                (entity2 instanceof Car || entity2 instanceof EnemyCar)) {
            handleVehicleVehicleCollision(entity1, entity2);
        }
    }
    private void handleTaxiVehicleCollision(Taxi taxi, Entity vehicle) {
        if (!taxi.isColliding() && !isVehicleColliding(vehicle)) {
            taxi.damage(getVehicleDamage(vehicle));
            damageVehicle(vehicle, TAXI_DAMAGE);  // Taxi inflicts 100 damage

            //Collision direction
            setCollisionDirections(taxi, vehicle);

            //Set both vehicles to have a timeout so multiple crashes do not occur instantly
            taxi.setColliding(true);
            taxi.setCollisionTimeoutFrames(COLLISION_TIMEOUT);

            setVehicleColliding(vehicle, true);
            setVehicleCollisionTimeoutFrames(vehicle, COLLISION_TIMEOUT);
        }
    }
    // Handles collision between a Driver and a vehicle
    private void handleDriverVehicleCollision(Driver driver, Entity vehicle) {
        if (!driver.isColliding() && !isVehicleColliding(vehicle)) {
            driver.damage(getVehicleDamage(vehicle));

            setCollisionDirections(driver, vehicle);

            driver.setColliding(true);
            driver.setCollisionTimeoutFrames(COLLISION_TIMEOUT);
            setVehicleColliding(vehicle, true);
            setVehicleCollisionTimeoutFrames(vehicle, COLLISION_TIMEOUT);
        }
    }
    //Handles collision between Passenger and a vehicle
    private void handlePassengerVehicleCollision(Passenger passenger, Entity vehicle) {
        if (!passenger.isColliding() && !isVehicleColliding(vehicle)) {
            passenger.damage(getVehicleDamage(vehicle));

            setCollisionDirections(passenger, vehicle);

            passenger.setColliding(true);
            passenger.setCollisionTimeoutFrames(COLLISION_TIMEOUT);
            setVehicleColliding(vehicle, true);
            setVehicleCollisionTimeoutFrames(vehicle, COLLISION_TIMEOUT);
        }
    }
    //Handles collision between vehicles
    private void handleVehicleVehicleCollision(Entity vehicle1, Entity vehicle2) {
        if (!isVehicleColliding(vehicle1) && !isVehicleColliding(vehicle2)) {
            damageVehicle(vehicle1, getVehicleDamage(vehicle2));
            damageVehicle(vehicle2, getVehicleDamage(vehicle1));

            setCollisionDirections(vehicle1, vehicle2);

            if (vehicle1 instanceof Car) {
                ((Car) vehicle1).setColliding(true);
            }
            if (vehicle2 instanceof Car) {
                ((Car) vehicle2).setColliding(true);
            }

        }
    }
    // Checks if the given vehicle is currently in a colliding state.
    private boolean isVehicleColliding(Entity vehicle) {
        if (vehicle instanceof Car) {
            return ((Car) vehicle).isColliding();
        } else if (vehicle instanceof EnemyCar) {
            return ((EnemyCar) vehicle).isColliding();
        } else {
            return false;
        }
    }

    private void setVehicleColliding(Entity vehicle, boolean colliding) {
        if (vehicle instanceof Car) {
            ((Car) vehicle).setColliding(colliding);
        }
    }

    private void setVehicleCollisionTimeoutFrames(Entity vehicle, int frames) {
        if (vehicle instanceof Car) {
            ((Car) vehicle).setCollisionTimeoutFrames(frames);
        } else if (vehicle instanceof EnemyCar) {
        }
    }

    //Damage based on type of vehicle
    private double getVehicleDamage(Entity vehicle) {
        if (vehicle instanceof Car) {
            return ((Car) vehicle).getDamage();
        } else if (vehicle instanceof EnemyCar) {
            return ((EnemyCar) vehicle).getDamage();
        } else {
            return 0;
        }
    }

    //Applies damage to given vehicle
    private void damageVehicle(Entity vehicle, double damage) {
        if (vehicle instanceof Car) {
            ((Car) vehicle).damage(damage);
        } else if (vehicle instanceof EnemyCar) {
            ((EnemyCar) vehicle).damage(damage);
        }
    }

    private void setEntityCollisionDirection(Entity entity, int directionX, int directionY) {
        if (entity instanceof Taxi) {
            ((Taxi) entity).setCollisionDirection(directionX, directionY);
        }
        if (entity instanceof Car) {
            ((Car) entity).setCollisionDirection(directionX, directionY);
        }
        if (entity instanceof Driver) {
            ((Driver) entity).setCollisionDirection(directionX, directionY);
        }
        if (entity instanceof Passenger) {
            ((Passenger) entity).setCollisionDirection(directionX, directionY);
        }
    }

    private void setCollisionDirections(Entity entity1, Entity entity2) {
        int directionX;
        int directionY;

        // Determine X direction based on the X coordinates of the entities
        if (entity1.getX() < entity2.getX()) {
            directionX = -1;
        } else {
            directionX = 1;
        }

        // Determine Y direction based on the Y coordinates of the entities
        if (entity1.getY() < entity2.getY()) {
            directionY = -1;
        } else {
            directionY = 1;
        }

        // Set collision directions for both entities
        setEntityCollisionDirection(entity1, directionX, directionY);
        setEntityCollisionDirection(entity2, -directionX, -directionY);
    }

    /**
     * Attempts to pick up a passenger if the taxi is adjacent to one.
     *
     * @param taxi The {@link Taxi} object representing the player's taxi in the game.
     */
    private void tryPickUpPassenger(Taxi taxi) {
        if (taxi.getDriver() == null || taxi.getHasPassenger()) {
            return;
        }

        Passenger passenger = checkTaxiPassengersAdjacency(taxi);
        if (passenger == null) {
            return;
        }

        // Check if the passenger was on a trip when ejected
        if (passenger.wasOnTripWhenEjected()) {
            TripEndFlag tripEndFlag = passenger.getTripEndFlag();
            if (tripEndFlag != null && passenger.getY() <= tripEndFlag.getY()) {
                // If the passenger has passed their trip end flag, pick them up and immediately drop them off
                tryMovePassengerToTaxi(passenger, taxi);
                tryDropOffPassenger(taxi);
            } else {
                // Continue the trip normally
                tryMovePassengerToTaxi(passenger, taxi);
            }
        } else {
            // Normal pickup process
            tryMovePassengerToTaxi(passenger, taxi);
        }
    }

    /**
     * Calculates the Euclidean distance between two points in 2D space.
     * <p>
     * This method computes the straight-line distance between two points (x1, y1) and (x2, y2)
     * using the Euclidean distance formula:
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The Euclidean distance between the two points.
     */
    public double calculateEuclideanDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Checks if the coin has collided with a taxi and handles the collision logic
    private void checkCoinCollision(Coin coin) {
        if (coin.isCoinCollided()) {
            return;
        }

        Taxi taxi = null;
        Driver driver = null;
        Passenger passenger = null;

        for (Entity entity : entities) {
            if (entity instanceof Taxi) {
                taxi = (Taxi) entity;
            } else if (entity instanceof Driver){
                driver = (Driver) entity;
            } else if (entity instanceof Passenger && ((Passenger) entity).isEjected) {
                passenger = (Passenger) entity;
            }
        }
        if (taxi == null) {
            return;
        }
        if (calculateEuclideanDistance(taxi.getX(), taxi.getY(), coin.getX(), coin.getY()) <= taxi.getTaxiRadius() + coin.getCOIN_RADIUS()) {
            coin.setCoinCollided(true);
            if (taxi.getPassenger() != null && !taxi.getPassenger().isHasCoin()) {
                taxi.getPassenger().increasePriority();
            }
            COIN_FRAMES = 0;
        }
        if (driver == null){
            return;
        }

        if (calculateEuclideanDistance(driver.getX(), driver.getY(), coin.getX(), coin.getY()) <= driver.getRadius() + coin.getCOIN_RADIUS() ){
            coin.setCoinCollided(true);
            if (passenger != null && !passenger.isHasCoin()) {
                passenger.increasePriority();
            }
            COIN_FRAMES = 0;

        }
    }

    // Checks if the invincible power-up has collided with either the taxi or the driver
    private void checkPowerUpCollision(InvinciblePower invinciblePower) {
        if (invinciblePower.isCollided()) {
            return;
        }

        Taxi taxi = null;
        Driver driver = null;

        for (Entity entity : entities) {
            if (entity instanceof Taxi) {
                taxi = (Taxi) entity;
            } else if (entity instanceof Driver) {
                driver = (Driver) entity;
            }
        }
        // Check if the taxi is present and if the taxi is close enough to the power-up to interact
        if (taxi != null && calculateEuclideanDistance(taxi.getX(), taxi.getY(), invinciblePower.getX(), invinciblePower.getY()) <= (taxi.getTaxiRadius() + invinciblePower.getRadius())) {
            invinciblePower.setCollided(true);
            taxi.makeInvincible(invinciblePower.getInvincibilityDuration());
            return;
        }
        // Check if the driver is present, is not in the taxi, and is close enough to the power-up to interact
        if (driver != null && !driver.isInTaxi() && calculateEuclideanDistance(driver.getX(), driver.getY(), invinciblePower.getX(), invinciblePower.getY()) <= invinciblePower.getRadius() + driver.getRadius()) {
            invinciblePower.setCollided(true);
            driver.makeInvincible(invinciblePower.getInvincibilityDuration());
        }
    }
    // Attempts to move the passenger to the taxi. The passenger will continue moving until their position matches the taxi's position exactly (as per Specification)
    private void tryMovePassengerToTaxi(Passenger passenger, Taxi taxi) {
        if (passenger.getX() == taxi.getX() && passenger.getY() == taxi.getY()) {
            pickUpPassenger(taxi, passenger);
            return;
        }
        movePassengerToTaxi(passenger, taxi);
    }

    private void pickUpPassenger(Taxi taxi, Passenger passenger) {
        //Flag changes
        taxi.setHasPassenger(true);
        passenger.setInTaxi(true);
        taxi.setPassenger(passenger);
        if (!passenger.wasOnTripWhenEjected()) {
            TripEndFlag tripEndFlag = new TripEndFlag(passenger, TRIPENDFLAG);
            entities.add(tripEndFlag);
            passenger.setTripEndFlag(tripEndFlag);
        }
        tripInProgress = true;
        firstPassengerPickedUp = true;

        // Reset priority and recalculate expected earnings when picked up
        lastTripPriority = passenger.getPriority();
        lastTripExpectedEarnings = passenger.getExpectedEarning();

        if (COIN_FRAMES < MAX_COIN_FRAMES) {
            passenger.increasePriority();
        }
    }

    private void movePassengerToTaxi(Passenger passenger, Taxi taxi) {
        // Move the passenger toward the taxi on the X axis
        if (passenger.getX() < taxi.getX()) {
            passenger.setX(passenger.getX() + passenger.getPassengerXMoveSpeed());
        } else if (passenger.getX() > taxi.getX()) {
            passenger.setX(passenger.getX() - passenger.getPassengerXMoveSpeed());
        }

        // Move the passenger toward the taxi on the Y axis
        if (passenger.getY() < taxi.getY()) {
            passenger.setY(passenger.getY() + passenger.getPassengerYMoveSpeed());
        } else if (passenger.getY() > taxi.getY()) {
            passenger.setY(passenger.getY() - passenger.getPassengerYMoveSpeed());
        }
    }

    private boolean checkTaxiPassengerAdjacent(Taxi taxi, Passenger passenger) {
        return calculateEuclideanDistance(taxi.getX(), taxi.getY(), passenger.getX(), passenger.getY()) <= PASSENGER_DETECT_TAXI_RADIUS;
    }

    //Checks if the taxi is adjacent to any passenger that has not yet completed their trip.
    private Passenger checkTaxiPassengersAdjacency(Taxi taxi) {
        //Iterate through entity, if passenger has completed their trip, just skip
        for (Entity entity : entities) {
            if (entity instanceof Passenger) {
                if (((Passenger) entity).getTripComplete()) {
                    continue;
                }
                if (checkTaxiPassengerAdjacent(taxi, (Passenger) entity)) {
                    return (Passenger) entity;
                }
            }
        }
        return null;
    }

    //Determines the action the taxi should take based on its current state and input.
    private void determineAction(Input input) {
        Taxi taxi = null;
        for (Entity entity : entities) {
            if (entity instanceof Taxi) {
                if (!((Taxi) entity).isPermanentlyDamaged()) {
                    taxi = (Taxi) entity;
                    break;
                }
            }
        }
        if (taxi == null) {
            return;
        }
        if (taxi.isMoving()) {
            return;
        }
        if (taxi.getHasPassenger()) {
            tryDropOffPassenger(taxi);
            return;
        }
        tryPickUpPassenger(taxi);
    }

    //Attempts to drop off the passenger if the taxi is near the trip end flag.
    private void tryDropOffPassenger(Taxi taxi) {
        TripEndFlag tripendflag = null;
        for (Entity entity : entities) {
            if (entity instanceof TripEndFlag) {
                tripendflag = (TripEndFlag) entity;
            }
        }
        if (tripendflag == null) {
            return;
        }

        if (taxi.getY() <= tripendflag.getY() || calculateEuclideanDistance(taxi.getX(), taxi.getY(), tripendflag.getX(), tripendflag.getY()) <= TRIP_END_FLAG_RADIUS) {
            dropOffPassenger(taxi, tripendflag);
        }
    }

    // Drops off the passenger at the trip end flag, updating the taxi and passenger states,
    // removes the passenger from the taxi, marks the trip as complete, calculates penalties
    private void dropOffPassenger(Taxi taxi, TripEndFlag tripendflag) {
        taxi.setHasPassenger(false);
        Passenger passenger = taxi.getPassenger();

        if (passenger == null){
            return;

        }
        passenger.setInTaxi(false);
        passenger.setMovingToFlag(true);
        passenger.setTripComplete(true);
        taxi.setPassenger(null);

        lastTripPenalty = calculatePenalty(taxi, tripendflag);
        lastTripExpectedEarnings = passenger.getExpectedEarning();
        lastTripPriority = passenger.getPriority();

        double tripEarnings = passenger.getExpectedEarning() - lastTripPenalty;
        if (tripEarnings < 0) {
            tripEarnings = 0;
        }
        totalScore += tripEarnings;
        tripInProgress = false;
    }

    //Displays the expected earnings and priority of the passenger on the screen if they are not in the taxi
    //and their trip is not complete.
    private void displayPassengerEarnings(Passenger passenger) {
        if (!passenger.isInTaxi() && !passenger.MovingToFlag() && !passenger.getTripComplete()) {
            double expectedEarnings = passenger.getExpectedEarning();

            // Coordinates for displaying the earnings and priority next to the passenger
            double xCoord = passenger.getX() - 100;
            double yCoord = passenger.getY();

            double priorityXCoord = passenger.getX() - 30;
            double priorityYCoord = passenger.getY();

            EARNINGS_FONT.drawString(String.format("%.1f", expectedEarnings), xCoord, yCoord);
            EARNINGS_FONT.drawString(String.format("%d", passenger.getPriority()), priorityXCoord, priorityYCoord);
        }
    }

    private void displayTripDetails() {
        if (!firstPassengerPickedUp) {
            return;
        }
        Passenger currentPassenger = getCurrentPassenger();
        boolean ongoingTrip = tripInProgress || (currentPassenger != null && currentPassenger.wasOnTripWhenEjected());

        if (ongoingTrip) {
            SCORE_FONT.drawString(CURRENT_TRIP, TRIPINFOCOORDX, TRIPINFOCOORDY);
            if (currentPassenger != null) {
                SCORE_FONT.drawString(String.format(EXPECTED_FEE + " %.1f", currentPassenger.getExpectedEarning()), TRIPINFOCOORDX, TRIPINFOCOORDY + 30);
                SCORE_FONT.drawString(String.format(PRIORITY + " %d", currentPassenger.getPriority()), TRIPINFOCOORDX, TRIPINFOCOORDY + 60);
            } else {
                // If currentPassenger is null, use the last known values
                SCORE_FONT.drawString(String.format(EXPECTED_FEE + " %.1f", lastTripExpectedEarnings), TRIPINFOCOORDX, TRIPINFOCOORDY + 30);
                SCORE_FONT.drawString(String.format(PRIORITY + " %d", lastTripPriority), TRIPINFOCOORDX, TRIPINFOCOORDY + 60);
            }
        } else {
            // Display last trip details
            SCORE_FONT.drawString(LAST_TRIP, TRIPINFOCOORDX, TRIPINFOCOORDY);
            SCORE_FONT.drawString(String.format(EXPECTED_FEE + " %.1f", lastTripExpectedEarnings), TRIPINFOCOORDX, TRIPINFOCOORDY + 30);
            SCORE_FONT.drawString(String.format(PRIORITY + " %d", lastTripPriority), TRIPINFOCOORDX, TRIPINFOCOORDY + 60);
            SCORE_FONT.drawString(String.format(PENALTY + " %.2f", lastTripPenalty), TRIPINFOCOORDX, TRIPINFOCOORDY + 90);
        }
    }

    private void drawHealthInfo() {
        Taxi activeTaxi = null;
        Driver driver = null;
        Passenger currentPassenger = null;

        // Find the active taxi, driver, and current passenger
        for (Entity entity : entities) {
            if (entity instanceof Taxi && !((Taxi) entity).isPermanentlyDamaged()) {
                activeTaxi = (Taxi) entity;
            } else if (entity instanceof Driver) {
                driver = (Driver) entity;
            } else if (entity instanceof Passenger && ((Passenger) entity).isInTaxi()) {
                currentPassenger = (Passenger) entity;
            }
        }

        // Display taxi health
        if (activeTaxi != null) {
            SCORE_FONT.drawString(String.format("TAXI %.2f", activeTaxi.getHealth()), TAXI_HEALTH_X, TAXI_HEALTH_Y);
        } else if (nextTaxi != null) {
            SCORE_FONT.drawString(String.format("TAXI %.2f", nextTaxi.getHealth()), TAXI_HEALTH_X, TAXI_HEALTH_Y);
        }

        // Display driver health
        if (driver != null) {
            SCORE_FONT.drawString(String.format("DRIVER %.2f", driver.getHealth()), DRIVER_HEALTH_X, DRIVER_HEALTH_Y);
        }

        // Display passenger health (current health value of the passenger on this trip. If there is no current trip,
        // the value shown will be the minimum health value of all the passengers)
        if (currentPassenger != null) {
            SCORE_FONT.drawString(String.format("PASSENGER %.2f", currentPassenger.getHealth()), PASSENGER_HEALTH_X, PASSENGER_HEALTH_Y);
        } else {
            double minPassengerHealth = getMinPassengerHealth();
            SCORE_FONT.drawString(String.format("PASSENGER %.2f", minPassengerHealth), PASSENGER_HEALTH_X, PASSENGER_HEALTH_Y);
        }
    }

    private double getMinPassengerHealth() {
        double minHealth = 100;
        for (Entity entity : entities) {
            if (entity instanceof Passenger) {
                Passenger passenger = (Passenger) entity;
                if (passenger.getHealth() < minHealth) {
                    minHealth = passenger.getHealth();
                }
            }
        }
        return minHealth;
    }

    //Gets current passenger from taxi
    private Passenger getCurrentPassenger() {
        for (Entity entity : entities) {
            if (entity instanceof Taxi && !((Taxi) entity).isPermanentlyDamaged()) {
                Taxi taxi = (Taxi) entity;
                Passenger passenger = taxi.getPassenger();
                if (passenger != null) {
                    return passenger;
                }
                // Check for ejected passengers
                Passenger ejectedPassenger = taxi.getEjectedPassenger();
                if (ejectedPassenger != null && ejectedPassenger.wasOnTripWhenEjected()) {
                    return ejectedPassenger;
                }
            } else if (entity instanceof Passenger) {
                Passenger passenger = (Passenger) entity;
                if (passenger.isEjected() && passenger.wasOnTripWhenEjected()) {
                    return passenger;
                }
            }
        }
        return null;
    }

    private double calculatePenalty(Taxi taxi, TripEndFlag tripEndFlag) {
        if (!taxi.isMoving() && taxi.getY() < tripEndFlag.getY()) {
            // Calculate the distance between the taxi and the trip end flag
            double distance = calculateEuclideanDistance(taxi.getX(), taxi.getY(), tripEndFlag.getX(), tripEndFlag.getY());

            // Check if the distance is greater than the radius of the trip end flag
            if (distance > TRIP_END_FLAG_RADIUS) {
                // Calculate the penalty
                return penaltyRate * (tripEndFlag.getY() - taxi.getY());
            }
        }
        return 0;
    }

    //If coin frames less than 500 (meaning they are active)
    private void drawCoinFrames() {
        if (COIN_FRAMES < MAX_COIN_FRAMES) {
            SCORE_FONT.drawString(String.format("%d", COIN_FRAMES), COIN_X, COIN_Y);
        }
    }

    //Decrease MAX frames incrementally
    private void decreaseMaxFrames() {
        if (MAX_FRAMES >= 0) {
            MAX_FRAMES--;
        }
    }

    //Increase coin frames incrementally
    private void increaseCoinFrames() {
        if (COIN_FRAMES < MAX_COIN_FRAMES) {
            COIN_FRAMES++;
        }
    }

    //Draw score from top left corner
    private void drawScore() {

        SCORE_FONT.drawString(String.format(PAY_EARNINGS + " %.2f", totalScore), EARNINGSX, EARNINGSY);
        SCORE_FONT.drawString(String.format(TARGET + " %.2f", SCORE_GOAL), TARGETX, TARGETY);
        SCORE_FONT.drawString(String.format(FRAMES_REM + " %d", MAX_FRAMES), MAXFRAMESX, MAXFRAMESY);
    }

    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }
}

