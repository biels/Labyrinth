package com.ru.tgra;

import com.ru.tgra.models.CubeMask;
import com.ru.tgra.models.Light;
import com.ru.tgra.models.Point3D;
import com.ru.tgra.models.Vector3D;
import com.ru.tgra.objects.*;
import com.ru.tgra.utilities.*;

import java.util.ArrayList;

public class GameManager
{
    public static ArrayList<GameObject> gameObjects;
    public static ArrayList<Spear> spears;

    public static MazeGenerator mazeGenerator;
    public static boolean[][] mazeWalls;
    public static boolean[][] mazeSpears;

    public static boolean mainMenu;

    public static Light headLight;
    public static Light endPointLight;
    public static Camera minimapCamera;
    public static Player player;

    private static Point3D endPointPos;
    private static int currentLevel;

    public static void init()
    {
        gameObjects = new ArrayList<GameObject>();
        spears = new ArrayList<Spear>();
        mazeGenerator = new MazeGenerator();
        currentLevel = 0;
    }

    public static void createMaze()
    {
        currentLevel++;

        spears.clear();
        gameObjects.clear();

        int sideLength = Settings.startSideLength + (Settings.sideLengthIncrement * (currentLevel - 1));

        generateMaze(sideLength);
        createFloor(new Point3D(sideLength / 2, -0.5f, sideLength / 2), sideLength);
        createWalls();
        createWatchtowers();
        createSpears();
        createEndPoint();
        createPlayer();
        createMinimap();
        createHeadLight();
        createEndPointLight();
    }

    public static void checkEndPoint()
    {
        int endPointX = (int)endPointPos.x;
        int endPointZ = (int)endPointPos.z;

        int playerX = player.getMazeX();
        int playerZ = player.getMazeY();

        if (playerX == endPointX && playerZ == endPointZ)
        {
            createMaze();
        }
    }

    private static void createEndPointLight()
    {
        endPointLight = new Light();
        endPointLight.setID(5);
        endPointLight.setColor(Settings.endPointLightColor);
        endPointLight.setPosition(endPointPos, false);
        endPointLight.setDirection(Settings.endPointLightDirection);
        endPointLight.setSpotFactor(Settings.endPointLightSpotFactor);
        endPointLight.setConstantAttenuation(Settings.endPointConstantAttenuation);
        endPointLight.setLinearAttenuation(Settings.endPointLightLinearAttenuation);
        endPointLight.setQuadraticAttenuation(Settings.endPointLightQuadraticAttenuation);
    }

    private static void createHeadLight()
    {
        headLight = new Light();
        headLight.setID(4);
        headLight.setColor(Settings.helmetLightColor);
        headLight.setSpotFactor(Settings.helmetLightSpotFactor);
        headLight.setConstantAttenuation(Settings.helmetConstantAttenuation);
        headLight.setLinearAttenuation(Settings.helmetLightLinearAttenuation);
        headLight.setQuadraticAttenuation(Settings.helmetLightQuadraticAttenuation);
    }

    private static void createMinimap()
    {
        minimapCamera = new Camera();
        minimapCamera.setOrthographicProjection(-5, 5, -5, 5, 3f, 100);
    }

    private static void createPlayer()
    {
        //player = new Player(new Point3D(mazeGenerator.getEnd()), new Vector3D(0.25f, 0.25f, 0.25f), Settings.playerSpeed, Settings.playerMinimapMaterial);
        player = new Player(new Point3D(mazeGenerator.getStart()), new Vector3D(0.25f, 0.25f, 0.25f), Settings.playerSpeed, Settings.playerMinimapMaterial);
        gameObjects.add(player);
    }

    private static void createEndPoint()
    {
        endPointPos = mazeGenerator.getEnd();

        EndPoint endPoint = new EndPoint(endPointPos, new Vector3D(0.25f, 0.25f, 0.25f), Settings.endPointMaterial, Settings.endPointMinimapMaterial, new CubeMask());
        gameObjects.add(endPoint);

        // DEBUG
        int x = (int)endPointPos.x;
        int y = (int)endPointPos.z;
        Point3D p = new Point3D();
        if (!mazeWalls[x+1][y])
        {
            p.x = x + 1;
            p.z = y;
        }
        else if (!mazeWalls[x-1][y])
        {
            p.x = x - 1;
            p.z = y;
        }
        else if (!mazeWalls[x][y+1])
        {
            p.x = x;
            p.z = y + 1;
        }
        else if (!mazeWalls[x][y-1])
        {
            p.x = x;
            p.z = y - 1;
        }

        p.y = Settings.spearUpY;

        Spear spear = new Spear(p, new Vector3D(0.1f, 1f, 0.1f), Settings.wallMaterial, Settings.wallMinimapMaterial);
        gameObjects.add(spear);
        spears.add(spear);
    }

    private static void createWalls()
    {
        Vector3D scale = new Vector3D(1f, 2f, 1f);
        int sideLength = mazeWalls.length;

        for (int i = 0; i < sideLength; i++)
        {
            for (int j = 0; j < sideLength; j++)
            {
                // Skip corners
                if ((i == 0 && j == 0) || (i == 0 && j == sideLength-1) ||
                    (i == sideLength-1 && j == 0) || (i == sideLength-1 && j == sideLength-1))
                {
                    continue;
                }

                if (mazeWalls[i][j])
                {
                    Point3D position = new Point3D(i, 0.5f, j);

                    /* === Cube mask === */
                    CubeMask mask = new CubeMask();
                    mask.setBottom(false);
                    mask.setTop(false);

                    // North
                    if (i != sideLength-1)
                    {
                        mask.setNorth(!mazeWalls[i+1][j]);
                    }
                    else
                    {
                        mask.setNorth(false);
                    }

                    // South
                    if (i != 0)
                    {
                        mask.setSouth(!mazeWalls[i-1][j]);
                    }
                    else
                    {
                        mask.setSouth(false);
                    }

                    // East
                    if (j != sideLength-1)
                    {
                        mask.setEast(!mazeWalls[i][j+1]);
                    }
                    else
                    {
                        mask.setEast(false);
                    }

                    // West
                    if (j != 0)
                    {
                        mask.setWest(!mazeWalls[i][j-1]);
                    }
                    else
                    {
                        mask.setWest(false);
                    }

                    Block block = new Block(position, new Vector3D(scale), Settings.wallMaterial, Settings.wallMinimapMaterial, mask);

                    gameObjects.add(block);
                }
            }
        }
    }

    private static void createSpears()
    {
        int sideLength = mazeSpears.length;
        Vector3D scale = new Vector3D(0.1f, 1f, 0.1f);

        for (int i = 0; i < sideLength; i++)
        {
            for (int j = 0; j < sideLength; j++)
            {
                if (mazeSpears[i][j])
                {
                    Point3D position = new Point3D(i, Settings.spearUpY, j);

                    Spear spear = new Spear(position, scale, Settings.wallMaterial, Settings.wallMinimapMaterial);
                    gameObjects.add(spear);
                    spears.add(spear);
                }
            }
        }
    }

    private static void createWatchtowers()
    {
        int max = mazeWalls.length-1;
        float towerPosY = 1.5f;

        Point3D[] positions = new Point3D[4];
        positions[0] = new Point3D(0f, towerPosY, 0f);
        positions[1] = new Point3D(0f, towerPosY, max);
        positions[2] = new Point3D(max, towerPosY, 0f);
        positions[3] = new Point3D(max, towerPosY, max);

        Vector3D[] directions = new Vector3D[4];
        directions[0] = new Vector3D(1f, -0.5f, 1f);
        directions[1] = new Vector3D(1f, -0.5f, -1f);
        directions[2] = new Vector3D(-1f, -0.5f, 1f);
        directions[3] = new Vector3D(-1f, -0.5f, -1f);

        for (int i = 0; i < 4; i++)
        {
            Point3D orbPos = new Point3D(positions[i]);
            orbPos.y = Settings.watchtowerScale.y;

            Light spotLight = new Light();
            spotLight.setID(i);
            spotLight.setPosition(orbPos, true);
            spotLight.setDirection(directions[i]);
            spotLight.setColor(Settings.watchtowerLightColor);
            spotLight.setSpotFactor(Settings.watchtowerLightSpotFactor);
            spotLight.setConstantAttenuation(Settings.watchtowerLightConstantAttenuation);
            spotLight.setLinearAttenuation(Settings.watchtowerLightLinearAttenuation);
            spotLight.setQuadraticAttenuation(Settings.watchtowerLightQuadraticAttenuation);

            spotLight.getPosition().y += 0.1f;

            Watchtower watchtower = new Watchtower
            (
                positions[i],
                Settings.watchtowerScale,
                Settings.watchtowerOrbScale,
                Settings.watchtowerMaterial,
                Settings.watchtowerMinimapMaterial,
                Settings.watchtowerOrbMaterial,
                Settings.watchtowerOrbMinimapMaterial,
                new CubeMask(),
                spotLight
            );

            gameObjects.add(watchtower);
        }
    }

    private static void createFloor(Point3D pos, float sideLength)
    {
        Block floor = new Block(pos, new Vector3D(sideLength, 0.01f, sideLength), Settings.floorMaterial, Settings.floorMinimapMaterial, new CubeMask(false, false, false, false, true, false));
        gameObjects.add(floor);
    }

    private static void generateMaze(int sideLength)
    {
        mazeGenerator.generateMaze(sideLength);
        mazeWalls = mazeGenerator.getWalls();
        mazeSpears = mazeGenerator.getSpears();
    }
}
