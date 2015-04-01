package com.aesireanempire.eplus;

import com.aesireanempire.eplus.network.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;


@Mod(name = EnchantingPlusMod.MODNAME, modid = EnchantingPlusMod.MODID, version = EnchantingPlusMod.VERSION, modLanguage = "java")
public class EnchantingPlusMod {

    public static final String MODNAME = "Enchanting Plus";
    public static final String MODID = "eplus";
    public static final String VERSION = "3.1.0a1";

    @SidedProxy(clientSide = "com.aesireanempire.eplus.network.ClientProxy", serverSide = "com.aesireanempire.eplus.network.CommonProxy")
    public static CommonProxy proxy  = null;
}
