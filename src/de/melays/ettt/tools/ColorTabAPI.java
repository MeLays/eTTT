/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;

public class ColorTabAPI {
	
	static Map< UUID, String > tabTeam = new HashMap<>();
	
	public static void setHeaderAndFooter( List< String > headerLines, List< String > footerLines, Collection< ? extends Player > receivers ) {
		try{
			if( headerLines == null ) headerLines = new ArrayList<>();
			if( footerLines == null ) footerLines = new ArrayList<>();
			
			try {
				Constructor<?> constructor = getNMSClass( "PacketPlayOutPlayerListHeaderFooter" ).getConstructor();
				Object packet = constructor.newInstance();
							
				Object headerComponent = getNMSClass( "IChatBaseComponent" ).getDeclaredClasses()[ 0 ].getMethod( "a", String.class ).invoke( null, "{\"text\":\"" + listToString( headerLines ) + "\"}" );
				Object footerComponent = getNMSClass( "IChatBaseComponent" ).getDeclaredClasses()[ 0 ].getMethod( "a", String.class ).invoke( null, "{\"text\":\"" + listToString( footerLines )+ "\"}" );
				 
				setField( packet, "a", headerComponent );
				setField( packet, "b", footerComponent );
				
				for( Player t : receivers ) sendPacket( t, packet );
			} catch ( Exception e ) {
				
			}
		}
		catch ( Exception e ) {
			
		}
	}
	
	private static String listToString( List< String > list ) {
		String output = "";
		for( String s : list ) {
			output += s.replace( "&", "�" ) + "\n";
		}
		return output.length() > 0 ? output.substring( 0, output.length() -1 ) : output;
	}
	
	
	public static void setTabStyle( Player p, String prefix, String suffix, int priority, Collection< ? extends Player > receivers ) {
		try{
			if( prefix == null ) prefix = "";
			if( suffix == null ) suffix = "";
			
			if( prefix.length() > 16 ) prefix = prefix.substring( 0, 16 );
			if( suffix.length() > 16 ) suffix = suffix.substring( 0, 16 );
			
			try {
				String teamName = priority + p.getName();
				
				if( teamName.length() > 16 ) teamName = teamName.substring( 0, 16 );
				
				Constructor< ? > constructor = getNMSClass( "PacketPlayOutScoreboardTeam" ).getConstructor();
				Object packet = constructor.newInstance();
				
				getNMSClass("ChatComponentText");
				
				Constructor< ? > ChatComponentTextConstructor = getNMSClass("ChatComponentText").getConstructor(String.class);
				
				List< String > contents = new ArrayList<>();
				contents.add( p.getName() );
				try {
					//1.13+
	                setField( packet, "a", teamName );
	                setField( packet, "b", ChatComponentTextConstructor.newInstance(teamName));//new ChatComponentText("")
	                setField( packet, "c", ChatComponentTextConstructor.newInstance(Main.c(prefix)) );//new ChatComponentText("")
	                setField( packet, "d", ChatComponentTextConstructor.newInstance(Main.c(suffix)) );//new ChatComponentText("")
	                setField( packet, "e", "ALWAYS" );
	                setField( packet, "i", 0 );
	                setField( packet, "h", contents );
				} catch( Exception ex ) {
					ex.printStackTrace();

		            try {
		            	//1.9+
		                setField( packet, "a", teamName );
		                setField( packet, "b", teamName );//new ChatComponentText("")
		                setField( packet, "c", Main.c(prefix) );//new ChatComponentText("")
		                setField( packet, "d", Main.c(suffix) );//new ChatComponentText("")
		                setField( packet, "e", "ALWAYS" );
		                setField( packet, "i", 0 );
		                setField( packet, "h", contents );
		            } catch( Exception ex2 ) {
		            	//1.8
		                setField( packet, "a", teamName );
		                setField( packet, "b", teamName );
		                setField( packet, "c", Main.c(prefix));
		                setField( packet, "d", Main.c(suffix) );
		                setField( packet, "e", "ALWAYS" );
		                setField( packet, "h", 0 );
		                setField( packet, "g", contents );
		            }
				}
				for( Player t : receivers ) sendPacket( t, packet );
				tabTeam.put( p.getUniqueId(), teamName);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static void clearTabStyle( Player p, Collection< ? extends Player > receivers ) {
		try{
		
			if( !tabTeam.containsKey( p.getUniqueId() ) )
				tabTeam.put( p.getUniqueId(), "nothing" );
			
			String teamName = tabTeam.get( p.getUniqueId() );
			
			try {
				Constructor< ? > constructor = getNMSClass( "PacketPlayOutScoreboardTeam" ).getConstructor();
				Object packet = constructor.newInstance();
				
				Constructor< ? > ChatComponentTextConstructor = getNMSClass("ChatComponentText").getConstructor(String.class);
	
				List< String > contents = new ArrayList<>();
				contents.add( p.getName() );
				try {
					//1.13
	                setField( packet, "a", teamName );
	                setField( packet, "b", ChatComponentTextConstructor.newInstance(teamName) );
	                setField( packet, "e", "ALWAYS" );
	                setField( packet, "i", 1 );
	                setField( packet, "h", contents );
				} catch( Exception ex ) {
					ex.printStackTrace();
		            try {
		                setField( packet, "a", teamName );
		                setField( packet, "b", teamName );
		                setField( packet, "e", "ALWAYS" );
		                setField( packet, "h", 1 );
		                setField( packet, "g", contents );
		            } catch( Exception ex2 ) {
		                setField( packet, "a", teamName );
		                setField( packet, "b", teamName );
		                setField( packet, "e", "ALWAYS" );
		                setField( packet, "i", 1 );
		                setField( packet, "h", contents );
		            }
				}
				for( Player t : receivers ) sendPacket( t, packet );
				tabTeam.put( p.getUniqueId(), teamName );
			} catch ( Exception e ) {
				
			}
		}
		catch ( Exception e ) {
			
		}
	}
	
    public static Field modifiers = getField( Field.class, "modifiers" );

    public static Class< ? > getNMSClass( String name ) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
        try {
            return Class.forName( "net.minecraft.server." + version + "." + name );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket( Player to, Object packet ) {
        try {
            Object playerHandle = to.getClass().getMethod( "getHandle" ).invoke( to );
            Object playerConnection = playerHandle.getClass().getField( "playerConnection" ).get( playerHandle );
            playerConnection.getClass().getMethod( "sendPacket", getNMSClass( "Packet" ) ).invoke( playerConnection, packet );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void setField( Object change, String name, Object to ) throws Exception {
        Field field = change.getClass().getDeclaredField( name );
        field.setAccessible( true );
        field.set( change, to );
        field.setAccessible( false );
    }

    public static Field getField( Class< ? > clazz, String name ) {
        try {
            Field field = clazz.getDeclaredField( name );
            field.setAccessible( true );
            if( Modifier.isFinal( field.getModifiers() ) ) {
                modifiers.set( field, field.getModifiers() & ~Modifier.FINAL );
            }
            return field;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
    }
}
