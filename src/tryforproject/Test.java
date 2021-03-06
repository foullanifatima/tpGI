package projet;

/*
 * Author: Anthony Sulistio
 * Date: November 2004
 * Description: A simple program to demonstrate of how to use GridSim 
 *              network extension package.
 *              This example shows how to create two GridSim entities and
 *              connect them via a link. NetUser entity sends messages to
 *              Test entity and Test entity sends back these messages.
 */
 
import java.util.*;
import gridsim.*;
import gridsim.net.*;
import gridsim.util.SimReport;
import eduni.simjava.*;


/**
 * This class handles incoming requests and sends back an ack.
 * In addition, this class logs every activities.
 */
public class Test extends GridSim
{
    private int myID_;          // my entity ID
    private String name_;       // my entity name
    private String destName_;   // destination name
    private int destID_;        // destination id
    private SimReport report_;  // logs every activity

    /**
     * Creates a new NetUser object
     * @param name      this entity name
     * @param destName  the destination entity's name
     * @param link      the physical link that connects this entity to destName
     * @throws Exception    This happens when name is null or haven't 
     *                      initialized GridSim.
     */
    public Test(String name, String destName, Link link) throws Exception
    {
        super(name, link);

        // get this entity name from Sim_entity
        this.name_ = super.get_name();

        // get this entity ID from Sim_entity
        this.myID_ = super.get_id();

        // get the destination entity name
        this.destName_ = destName;
        
        // logs every activity. It will automatically create name.csv file
        report_ = new SimReport(name);
        report_.write("Creates " + name);
    }

    /**
     * The core method that handles communications among GridSim entities.
     */
    public void body()
    {
        // get the destination entity ID
        this.destID_ = GridSim.getEntityId(destName_);

        int packetSize = 500;   // packet size in bytes
        Sim_event ev = new Sim_event();     // an event
        int [][] a =null;
        int [][] b = null;
        int i=0;
        // a loop waiting for incoming events
        while ( Sim_system.running() )
        {
            // get the next event from the Input buffer
            super.sim_get_next(ev);
            
            // if an event denotes end of simulation
            if (ev.get_tag() == GridSimTags.END_OF_SIMULATION)
            {
                System.out.println();
                write(super.get_name() + ".body(): exiting ...");
                break;
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////
            // if an event denotes another event type
            else if (ev.get_tag() == NetUser.SEND_MSG)
            {
                System.out.println();
               int [][] msg=(int[][]) ev.get_data();
                if (i==0) a=msg; //matrice 1
                else b=msg;      //matrice2
                i=i+1;           
                write( name_+".body(): receive " +Arrays.deepToString(msg));
                write("a="+Arrays.deepToString(a));
                write("b="+Arrays.deepToString(b));
               // sends back an ack
               
                IO_data data = new IO_data(ev.get_data(), packetSize, destID_);
                write(name_ + ".body(): Sending back ack"  );
              
                // sends through Output buffer of this entity
                super.send(super.output, GridSimTags.SCHEDULE_NOW,
                           NetUser.SEND_MSG, data);
                
                
                // call fonction pour calculer produit matriciel
if(i>1){
        int[][] res= produit(a,b);
        write("res="+Arrays.deepToString(res));
        IO_data data1 = new IO_data(res, packetSize, destID_);
         super.send(super.output, GridSimTags.SCHEDULE_NOW,
                           NetUser.SEND_MSG, data1);
        write(name_ + ".body(): Sending back result"  );}
            }
             ////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////
            // handle a ping requests. You need to write the below code
            // for every class that extends from GridSim or GridSimCore.
            // Otherwise, the ping functionality is not working.
            else if (ev.get_tag() ==  GridSimTags.INFOPKT_SUBMIT)
            {
                processPingRequest(ev);                
            }
           
        }

        ////////////////////////////////////////////////////////
        // shut down I/O ports
        shutdownUserEntity();
        terminateIOEntities();

        // don't forget to close the file
        if (report_ != null) {
            report_.finalWrite();
        }
        
        System.out.println(this.name_ + ":%%%% Exiting body() at time " +
                           GridSim.clock() );
    }

    
    
    
    
    /************************** add fct produit matricie********************************/
   
    /**
     * Handles ping request
     * @param ev    a Sim_event object
     */
    private void processPingRequest(Sim_event ev)
    {
        InfoPacket pkt = (InfoPacket) ev.get_data();
        pkt.setTag(GridSimTags.INFOPKT_RETURN);
        pkt.setDestID( pkt.getSrcID() );

        // sends back to the sender
        super.send(super.output, GridSimTags.SCHEDULE_NOW,
                   GridSimTags.INFOPKT_RETURN,
                   new IO_data(pkt,pkt.getSize(),pkt.getSrcID()) );
    }
    
    /**
     * Prints out the given message into stdout.
     * In addition, writes it into a file.
     * @param msg   a message
     */
    private void write(String msg)
    {
        System.out.println(msg);
        if (report_ != null) {
            report_.write(msg);
        }        
    }
     public int[][]  produit(int[][] a, int[][]b)
    {
    	 int number_of_rows_first_matrix = a.length;
    	 int number_of_columns_second_matrix = b[0].length;
    	 int number_of_columns_first_matrix = a[0].length;
    	 
         int C[][] = new int[number_of_rows_first_matrix][number_of_columns_second_matrix];
         //multiplication
    for(int i=0; i<number_of_rows_first_matrix; i++){
      for(int j=0; j<number_of_columns_second_matrix; j++){ 
        C[i][j] = 0;    
        for(int k=0; k<number_of_columns_first_matrix ;k++)    
        { 
          C[i][j] += a[i][k] * b[k][j];    
        }
         
      }
    }  
        return C;
    }
    
} // end class

