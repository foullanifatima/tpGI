package tryforproject;

/*
 * Author: Anthony Sulistio
 * Date: November 2004
 * Description: A simple program to demonstrate of how to use GridSim 
 *              network extension package.
 *              This example shows how to create two GridSim entities and
 *              connect them via a link. NetUser entity sends messages to
 *              Test entity and Test entity sends back these messages.
 */

import gridsim.*;
import gridsim.net.*;
import eduni.simjava.*;
import java.util.*;
import java.util.Scanner;


/**
 * This class basically sends one or more messages to the other
 * entity over a link. Then, it waits for an ack.
 * Finally, before finishing the simulation, it pings the other
 * entity.
 */
public class NetUser extends GridSim
{
    private int myID_;          // my entity ID
    private String name_;       // my entity name
    private String destName_;   // destination name
    private int destID_;        // destination id
    
    /** Custom tag that denotes sending a message */
    public static final int SEND_MSG = 1;


    /**
     * Creates a new NetUser object
     * @param name      this entity name
     * @param destName  the destination entity's name
     * @param link      the physical link that connects this entity to destName
     * @throws Exception    This happens when name is null or haven't 
     *                      initialized GridSim.
     */
    public NetUser(String name, String destName, Link link) throws Exception
    {
        super(name, link);

        // get this entity name from Sim_entity
        this.name_ = super.get_name();

        // get this entity ID from Sim_entity
        this.myID_ = super.get_id();

        // get the destination entity name
        destName_ = destName;
    }

    /**
     * The core method that handles communications among GridSim entities.
     */
    public void body()
    {
        int packetSize = 500;   // packet size in bytes
        int size = 3;           // number of packets sent
        int i = 0;

        // get the destination entity ID
        this.destID_ = GridSim.getEntityId(destName_);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // sends messages over the other side of the link
        
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////partie vérification + entrer les tailles des matrices
int n,p,s,m;
        
        
        do {
			Scanner scanner = new Scanner(System.in);
			
			System.out.println("Enter the number of rows of the first matrix : ");
			n= scanner.nextInt();
			
			System.out.println("Enter the number of columns of the first matrix : ");
			p = scanner.nextInt();
			
			System.out.println("Enter the number of rows of the second matrix : ");
			s= scanner.nextInt();
			
			System.out.println("Enter the number of columns of the second matrix : ");
			m= scanner.nextInt();
			
			if(p!=s) {
				System.out.println("Remember that the number of columns of the first matrix "
		        		+ "and the number of rows of the second matrix must be equal");
			}
			
		} while (p!=s);


// appel de fonction lirematrice pour entrer les valeurs des matrices

         int[][] msg1 = lirematrice(n,p);
         int[][] msg2 = lirematrice(p,m);



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       //sending msg1
            IO_data data = new IO_data(msg1, packetSize, destID_);
            System.out.println(name_ + ".body(): Sending " + Arrays.deepToString(msg1) +
                ", at time = " + GridSim.clock() );

            // sends through Output buffer of this entity
            super.send(super.output, GridSimTags.SCHEDULE_NOW,
                       NetUser.SEND_MSG, data);
       
            
         //sending msg2   
             IO_data data2 = new IO_data(msg2, packetSize, destID_);
            System.out.println(name_ + ".body(): Sending " + Arrays.deepToString(msg2) +
                ", at time = " + GridSim.clock() );

            // sends through Output buffer of this entity
            super.send(super.output, GridSimTags.SCHEDULE_NOW,
                       NetUser.SEND_MSG, data2);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////
          Object obj = null;
            for (i = 0; i < size; i++)
        {
            // waiting for incoming event in the Input buffer
            obj = super.receiveEventObject();
            System.out.println(name_ + ".body(): Receives Ack for " + Arrays.deepToString((int[][])obj));
        }    
           
              

        ////////////////////////////////////////////////////////
        // ping functionality
        InfoPacket pkt = null;

        // There are 2 ways to ping an entity:
        // a. non-blocking call, i.e.
        //super.ping(destID_, size);    // (i)   ping
        //super.gridSimHold(10);        // (ii)  do something else
        //pkt = super.getPingResult();  // (iii) get the result back

        // b. blocking call, i.e. ping and wait for a result
        pkt = super.pingBlockingCall(destID_, size);

        // print the result
        System.out.println("\n-------- " + name_ + " ----------------");
        System.out.println(pkt);
        System.out.println("-------- " + name_ + " ----------------\n");

        ////////////////////////////////////////////////////////
        // sends back denoting end of simulation
        super.send(destID_, GridSimTags.SCHEDULE_NOW,
                   GridSimTags.END_OF_SIMULATION);

        ////////////////////////////////////////////////////////
        // shut down I/O ports
        shutdownUserEntity();
        terminateIOEntities();

        System.out.println(this.name_ + ":%%%% Exiting body() at time " +
                           GridSim.clock() );
    }

    //////////////////////////////////////
    //fonction pour lire les données d'une matrice
    public int[][] lirematrice(int n , int m)
    {
        int i,j;
        //declarer la mtrice
        int result[][]= new int[n][m];
        Scanner in=null;
        try {
        in=new Scanner(System.in);
        //entrer les valeurs
        System.out.println("les valeurs de matrice sont :");
        for (i = 0; i < n; i++)
                for (j = 0; j < m; j++)
                    result[i][j] = in.nextInt();
        
        //afficher la matrice
         System.out.println("***** La matrice *****");
         System.out.println(Arrays.deepToString(result));
         System.out.println("**********************************");
         
        }
        catch(Exception e){   
        }
        finally{
         // in.close();
        }
       return result;
    }
    
    
} // end class

