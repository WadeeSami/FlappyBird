package com.wadeesami.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class flappy extends ApplicationAdapter {
    int score;
    int scoringTube = 0;
    BitmapFont result;
    String DEBUG = "WADEE_FLAPPY";
    SpriteBatch batch;
    Texture background;
    Texture birds[];
    Texture upperTube;
    Texture lowerTube;
    Texture gameOverTexture;
    Random randomOffset;
    Circle birdCircle;
    //ShapeRenderer renderer;
    Rectangle[] upperTubeRectangle;
    Rectangle[] lowerTubeRectangle;
    int flaggedBird = 1;
    float posY = 0;
    int gameState = 0;
    float velocity = -20;
    float gravity = 2;
    float gap = 400;
    float tubeOffset[];
    float tubeX[];
    float leftVelocity = 5;
    int numberOfTubes = 4;
    float distanceBetweenTubes;

    //if we want to save the game state, we should use the pause method
    //and resume when resuming from after the pause state
    @Override
    public void create() {
        score = 0;
        result = new BitmapFont();
        result.setColor(Color.BLACK);
        result.getData().setScale(10);
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        //create an array of textures to view 2 birds
        birds = new Texture[2];
        birds[0] = new Texture(Gdx.files.internal("bird.png"));
        birds[1] = new Texture(Gdx.files.internal("bird2.png"));
        upperTube = new Texture("toptube.png");
        lowerTube = new Texture("bottomtube.png");
        gameOverTexture = new Texture("gameover.png");

        tubeOffset = new float[numberOfTubes];
        randomOffset = new Random();
        tubeX = new float[numberOfTubes];
        birdCircle = new Circle();
        upperTubeRectangle = new Rectangle[numberOfTubes];
        lowerTubeRectangle = new Rectangle[numberOfTubes];
        //renderer = new ShapeRenderer();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;
        Gdx.app.log(DEBUG, "The width of the screen is " + Gdx.graphics.getWidth());

            startGame();
    }

    public void startGame(){
        posY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;//initial vertical position
        for (int i = 0; i < numberOfTubes; i++) {

            tubeX[i] = (Gdx.graphics.getWidth() / 2 - upperTube.getWidth() / 2 + Gdx.graphics.getWidth()) + (i * distanceBetweenTubes);
            Gdx.app.log(DEBUG, "tubeX with i: " + i + "is : " + tubeX[i]);

            //and a branch of the loop to initialize the rectangle variables
            upperTubeRectangle[i] = new Rectangle();
            lowerTubeRectangle[i] = new Rectangle();
        }

    }

    //this method is called each time the system draws the graphics
    @Override
    public void render() {

        if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
            score++;
            Gdx.app.log(DEBUG , "score is " + score);
            if (scoringTube < numberOfTubes - 1) {
                scoringTube++;
            } else {
                scoringTube = 0;
            }
        }
        batch.begin();
        //drawing goes here
        //draw the background here

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;

            }
        } else if(gameState == 1) {

            if (posY > 0 ) {//|| velocity < 0
                velocity += gravity;
                posY -= velocity;
            }else{
                gameState = 2;
            }

            if (Gdx.input.justTouched()) {
                velocity = -30;

            }
            for (int i = 0; i < numberOfTubes; i++) {
                tubeX[i] -= leftVelocity;
                //check if it goes out of the screen
                if (tubeX[i] < -lowerTube.getWidth()) {
                    Gdx.app.log(DEBUG, tubeX + " is less than 0");
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomOffset.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                }
                batch.draw(upperTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(lowerTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - lowerTube.getHeight() + tubeOffset[i]);
            }
        }else if(gameState == 2){
            batch.draw(gameOverTexture , Gdx.graphics.getWidth()/2 - gameOverTexture.getWidth()/2 , Gdx.graphics.getHeight()/2 - gameOverTexture.getHeight()/2);
            if (Gdx.input.justTouched()) {
                gameState = 1;
                startGame();
                velocity = 0 ;
                score = 0 ;
                scoringTube = 0;
            }
        }

        if (flaggedBird == 0) {
            flaggedBird = 1;//the flag used to specify the bird's drawn image
        } else flaggedBird = 0;

        birdCircle.set(Gdx.graphics.getWidth() / 2, posY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);
        //loop to specify the location of each rectangle
        for (int i = 0; i < numberOfTubes; i++) {
            float heightOfRect = Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i];
            upperTubeRectangle[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], upperTube.getWidth(), upperTube.getHeight());
            lowerTubeRectangle[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - lowerTube.getHeight() + tubeOffset[i], upperTube.getWidth(), upperTube.getHeight());
        }
        batch.draw(birds[flaggedBird], Gdx.graphics.getWidth() / 2 - birds[flaggedBird].getWidth() / 2, posY);
        result.draw(batch , String.valueOf(score),100 , 200 );
        batch.end();
        // renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < numberOfTubes; i++) {
            //  renderer.rect(upperTubeRectangle[i].x, upperTubeRectangle[i].y, upperTubeRectangle[i].width, upperTubeRectangle[i].height);
            // renderer.rect(lowerTubeRectangle[i].x, lowerTubeRectangle[i].y, lowerTubeRectangle[i].width, lowerTubeRectangle[i].height);
            if (Intersector.overlaps(birdCircle, upperTubeRectangle[i]) || Intersector.overlaps(birdCircle, lowerTubeRectangle[i])) {
                gameState = 2;
            }


        }
       /* renderer.setColor(com.badlogic.gdx.graphics.Color.RED);
        renderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        renderer.end();
        */

    }
}
