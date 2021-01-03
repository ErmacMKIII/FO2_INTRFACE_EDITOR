/*
 * Copyright (C) 2021 Alexander Stojanovich <coas91@rocketmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package rs.alexanderstojanovich.fo2ie.main;

import rs.alexanderstojanovich.fo2ie.intrface.Configuration;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class GameTime extends Thread {

    private boolean finished = false;

    public static final int TPS = Configuration.getInstance().getAnimationTicks();
    private double gameTicks = 0.0;

    private static GameTime instance;

    public static GameTime getInstance() {
        if (instance == null) {
            instance = new GameTime();
        }
        return instance;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (!finished) {
            long currTime = System.nanoTime();
            long deltaTime = currTime - lastTime;
            lastTime = currTime;

            gameTicks += TPS * (deltaTime / 1E9D);

            if (gameTicks >= TPS) {
                gameTicks = 0.0;
            }

        }

    }

    /**
     * Gets game ticks in interval [0, TPS) cycled
     *
     * @return game ticks
     */
    public double getGameTicks() {
        return gameTicks;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

}
