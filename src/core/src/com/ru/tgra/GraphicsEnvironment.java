package com.ru.tgra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GraphicsEnvironment
{
    public static Shader shader;

    public static void init()
    {
        shader = new Shader();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    }
}
