package com.example.luci.roamappdevelopment;

import java.util.UUID;

/**
 * Created by LUCI on 11/2/2017.
 */

public class IDGenerator
{
    public static IDGenerator instance;
    private UUID generator;

    private IDGenerator()
    {
        generator = new UUID(0,0);
    }

    public static IDGenerator getInstance()
    {
        if(instance == null)
        {
            instance = new IDGenerator();
        }
        return instance;
    }
    public String generateID()
    {
        return generator.randomUUID().toString();
    }
}
