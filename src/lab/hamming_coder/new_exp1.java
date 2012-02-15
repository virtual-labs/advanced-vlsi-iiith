import java.util.*;
import java.util.PropertyPermission.*;
import java.awt.*;
import java.awt.Color.*;
import java.awt.MediaTracker.*;
import java.awt.event.*;
import java.text.*;
import java.awt.datatransfer.*;
import java.net.*;
import java.net.URLEncoder.*;
import java.io.*;
import java.io.File.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.ImageIcon.*;
public class exp1 extends JApplet
{
	public  void init()
	{
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
				}
				});
	}
	private  void createAndShowGUI() {

		MyPanel myPane = new MyPanel();
		myPane.setOpaque(true);
		setContentPane(myPane);
	}
	public class MyPanel  extends JPanel  implements ActionListener//MouseListener,MouseMotionListener
	{
		int exp_type = 0 ; // 0 -> Complementary Inverter , 1 -> Pseudo Inverter 
		/** Work Panel Variables ************************************************************************************************/
		double scale_x = 1 ;  // scaling of work pannel 
		double scale_y = 1 ;
		int xor_delete_count=0;
		int [] xor_delete=new int[200];
		int pmos_delete_count=0;
		int [] pmos_delete=new int[200];
		int nmos_delete_count=0;
		int [] nmos_delete=new int[200];
		int gnd_delete_count=0;
		int [] gnd_delete=new int[200];
		int input_delete_count=0;
		int [] input_delete=new int[200];
		int output_delete_count=0;
		int [] output_delete=new int[200];
		int vdd_delete_count=0;
		int [] vdd_delete=new int[200];

		int pmos_count=0;	
		int xor_count=0;	
		int nmos_count=0;	
		int gnd_count=0;	
		int input_count=0;	
		int output_count=0;	
		int vdd_count=0;	

		int work_x ;
		int work_y ;
		int wire_button  = 0 ;// 0 not presed already , 1 -> already pressed 
		int img_button_pressed = -1 ;
		int simulate_button_pressed = -1;
		int draw_work = 0 ; // if 1 -> draw the image on work 

		int[][] work_mat ;   // if -1 => no comp is there on mat .. if i the (i)th comp of node_comp is present
		int[][] end_points_mat ;   // 
		int[][] wire_mat ;   // if -1 => no comp is there on mat .. if i the (i)th comp of node_comp is present
		int[][] wire_points_mat ;   // 

		int work_img_width = 50;
		int work_img_height = 50;
		int work_panel_width = 1400;
		int work_panel_height = 1400;

		int node_drag = -1 ; // it rep the index of comp_node which is selected to be draged 
		int wire_drag = -1 ; // it rep the index of wire which is selected to be extented from its end 
		int wire_drag_end = 1 ; // from which end it should be draged 
		int[] comp_count = new int[20] ; //  comp_count[i] represents the count of ( comp"i".jpg ) component ..
		int total_comp = 0 ;
		//              node[] comp_node = new node[20];
		node[] comp_node = new node[200];

		int total_wire = 0 ;
		              line[] wire = new line[200];

		// Dialog Box -----------------------------------
		//              myDialog[] dialog = new myDialog[14]  ; //in this exp at max 6 comp can be used  (I assume that comp is used once )
		//              JFrame[] fr = new JFrame[14] ;
		String[] comp_str = {           // This will store what should at the Dialog Box for each component
			"This is shows which component is selected ." ,
			"XOR ", "Ground Terminal " ," Wire ",  // 1, 2 , 3
			"INPUT " ," ", " " , // 4 , 5 ,6

			"NMOS", "Capacitor" ,"Vdd ",  // 7 , 8 , 9 
			"OUTPUT",  " " ,"Inductor ",  // 10, 11 , 12
			"This is CMOS Chip No:1" ,"This is CMOS Chip No:1"};

		//*************************************************************************************************************************************
		//Circuit Component values which need to be send to ngspice ***************************************************************************

		String Pmos_l = "50n";
		String Pmos_w = "100n";

		String Nmos_l = "50n";
		String Nmos_w = "100n";

		String Capacitance = "100";
		String hori_len;
		String veri_len;
		//*************************************************************************************************************************************
		boolean simulate_flag = false ;
		Image img[] = new Image[20] ;
		ImageIcon icon[] = new ImageIcon[20] ;

		ImageIcon icon_simulate ;
		ImageIcon icon_graph ;

		MediaTracker mt ;



		URL base;
		JPanel topPanel = new JPanel () ;
		JButton simulate_button ;
		JButton graph_button ;
		JComboBox exp_list ;
		JButton layout_button ;

		JSplitPane splitPane ; // devides center pane into left and right panel 
		JPanel rightPanel = new JPanel();// = new exp1_graph();
		//      graph waveRightPanel = new graph() ;// = new exp1_graph();
		graph waveRightPanel ;//= new graph() ;// = new exp1_graph();
//
		JPanel leftPanel = new JPanel() ;
		JSplitPane leftSplitPane ;  // divides left Panel into ( tool Panel ) and (work panel )...
		JPanel toolPanel = new JPanel ();
		JPanel toolPanelUp ;
		JButton selected ;
		JPanel toolPanelDown ;
		JToolBar leftTool1 = new JToolBar(1);
		JButton img_button1[] = new JButton[10] ;
		JToolBar leftTool2 = new JToolBar(1);
		JButton img_button2[] = new JButton[10] ;
		WorkPanel workPanel = new WorkPanel();


		 public class graph2 extends JPanel implements MouseMotionListener,MouseListener
                {
                        String fileToRead = "outfile";

                        StringBuffer strBuff;
                        TextArea txtArea;
                        String myline;
                        JLabel l ;
			int xpos,ypos;
                        int[] A = new int[64] ;
                        int[] B = new int[64] ;
                        int[] C_in = new int[64] ;
                        int[] C_out = new int[64] ;
                        int[] S = new int[64] ;
                        int no_values = 0 ;
                        int count=0;
                        //public  graph()
                        public  void graph ()
                        {
				int i;
				System.out.println("xpos ");
				addMouseMotionListener(this);
				addMouseListener(this);
				for(i=0;i<63;i++)
				{
					 if(i%4==0 || i % 4==1)
						A[i]=1;
					else
						A[i]=0;
					if(i%8<4)
						B[i]=1;
					else
						B[i]=0;
					if(i%16<8)
						C_in[i]=1;
					else
						C_in[i]=0;
					if(i%16<3 || (i%16 > 8 && i %16 <  11))
						S[i]=1;
					else
						S[i]=0;
					if(i%16<3 ||(i%8>6 && i%16 < 8) ||(i%16>10 && i%16 < 15))
						C_out[i]=1;
					else
						C_out[i]=0;
				}
                        }
			public void make_grap()
			{
			}
			public void mouseMoved(MouseEvent me)
			{
				xpos = me.getX();
				ypos = me.getY();
			}
			public void mouseReleased (MouseEvent me) {
                        }
                        public void mouseEntered (MouseEvent me) {
                        }
                        public void mouseExited (MouseEvent me) {
                        }
                        public void mouseDragged (MouseEvent me) {
                        }
                        public void mousePressed (MouseEvent me) {
                        }
			   public void mouseClicked(MouseEvent me)
                        {
				int k;
				xpos = me.getX();
				ypos = me.getY();
				if(ypos>90 && ypos<140)
				{
				if(xpos < 108)
				{
					for(k=0;k<8;k++)
						C_in[k]=0;	
				make_graph("fre");
				}
				}
				System.out.println("xpos"+xpos);
				System.out.println("ypos"+ypos);
			}

			 public void make_graph(String file)
                        {
                               /* fileToRead = file;
                                URL url = null;
                                try
                                {
                                        url = new URL(getCodeBase(), fileToRead);
                                }
                                catch(MalformedURLException e){
                                        System.out.println("I did't got the outfile to read :( :( So I am very said ");
                                }
                                String line;
                                try{
                                        InputStream in = url.openStream();
                                        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                                        strBuff = new StringBuffer();
                                        myline = bf.readLine();
                                        while(!myline.equals("Values:"))
                                        {
                                                myline = bf.readLine();
                                        }
                                        int i = 0 ;
                                /*        while((line = bf.readLine()) != null){
                                                line = bf.readLine();
                                                line = bf.readLine();
                                                A[i] = Double.parseDouble(line);
                                                line = bf.readLine();
                                                line = bf.readLine();
                                                B[i] = Double.parseDouble(line);
                                                line = bf.readLine();
                                                line = bf.readLine();
                                                C_in[i] = Double.parseDouble(line);
                                                line = bf.readLine();
                                                line = bf.readLine();
                                                C_out[i] = Double.parseDouble(line);
                                                line = bf.readLine();
                                                line = bf.readLine();
                                                S[i] = Double.parseDouble(line);
                                                i++;
                                        }
                         		            no_values = i ;*/

                                        repaint();

                                        //              System.out.println("Hi I am in the contrct func of the exp1_graph class :)");
                               // }
                                 //       catch(IOException e){
                                   //             e.printStackTrace();
                                     //   }


                        }
				   public void paint(Graphics g)
                                {
					count++;

                                        System.out.println("Hi I am in the gpaint func of the exp1_graph class :)");
				System.out.println("xpos " + xpos);
                                        int i , j ;
                                        Graphics2D g2d = (Graphics2D)g ;
                                        g2d.setStroke(new BasicStroke(2));
                                        // back ground 
                                     //   g2d.setColor(new Color(204 , 255 , 255));
                                       // g2d.fillRect(0,0,1000,1500);
                                        g2d.setColor(Color.lightGray);
                                        for ( i = 0 ; i < 1500 ; i += 30 )
                                        {
                                                for (j = 0 ; j < 1500 ; j +=5 )
                                                {
                                                        g2d.fillOval(i , j , 2 ,1);
                                                }
                                        }
                                        for ( i = 0 ; i < 1500 ; i += 5 )
                                        {
                                                for (j = 0 ; j < 1500 ; j +=30 )
                                                {
                                                        g2d.fillOval(i , j , 2 , 1);
                                                }
                                        }


					         g2d.setColor(Color.red);
					int b;
					int a;
                                        g2d.drawString("CIn ",  5 , 90 );
					System.out.println("reached");
                                        for( i = 0 ; i < 8 ; i++ )
                                        {
					/*	b=(C_in[i]*50);
						a=(C_in[i+1]*50);
						if(b- a > 40)
						{
                                                g2d.drawLine(40+10*i , 140-b , 40 + 10*(i+1) , 140-b );
                                                g2d.drawLine(40+10*(i+1) , 140-b , 40 + 10*(i+1) , 140-a );
						}
						else if(a-b>40)
						{
                                                g2d.drawLine(40+10*i , 140-b , 40 + 10*(i+1) , 140-b );
                                                g2d.drawLine(40+10*(i+1) , 140-b , 40 + 10*(i+1) , 140-a );
						}
						else	*/
						if(i<8)
						{
					//	C_in[i] = 0;
					//	C_in[i+1] = 0;
						System.out.println(C_in[i]);
						}
                                                g2d.drawLine(40+10*i , 140-(C_in[i]*50) , 40 + 10*(i+1) , 140-(C_in[i+1]*50) );
                                        }
                                        /*g2d.drawString("B",  5 , 160 );
                                        for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(B[i]*50);
						a=(B[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 210-b , 40 + 10*(i+1) , 210-b );
                                                g2d.drawLine(40+10*(i+1) , 210-b , 40 + 10*(i+1) , 210-a );
						}
						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 210-b , 40 + 10*(i+1) , 210-b );
                                                g2d.drawLine(40+10*(i+1) , 210-b , 40 + 10*(i+1) , 210-a );
						}
						else
                                                g2d.drawLine(40+10*i , 210-(B[i]*50) , 40 + 10*(i+1) , 210-(B[i+1]*50) );
                                        }
                                        g2d.drawString("A", 5 , 230 );
                                        for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(A[i]*50);
						a=(A[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 280-b , 40 + 10*(i+1) , 280-b );
                                                g2d.drawLine(40+10*(i+1) , 280-b , 40 + 10*(i+1) , 280-a );
						}
						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 280-b , 40 + 10*(i+1) , 280-b );
                                                g2d.drawLine(40+10*(i+1) , 280-b , 40 + 10*(i+1) , 280-a );
						}
						else
                                                g2d.drawLine(40+10*i , 280-(A[i]*50) , 40 + 10*(i+1) , 280-(A[i+1]*50) );
                                        }
                                        g2d.setColor(Color.blue);
                                        g2d.drawString("COut ",  5, 310 );
                                        for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(C_out[i]*50);
						a=(C_out[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 350-b , 40 + 10*(i+1) , 350-b );
                                                g2d.drawLine(40+10*(i+1) , 350-b , 40 + 10*(i+1) , 350-a );
						}
						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 350-b , 40 + 10*(i+1) , 350-b );
                                                g2d.drawLine(40+10*(i+1) , 350-b , 40 + 10*(i+1) , 350-a );
						}
						else
                                                g2d.drawLine(40+10*i , 350-(C_out[i]*50) , 40 + 10*(i+1) , 350-(C_out[i+1]*50) );
                                        }
                                        g2d.drawString("Sum ",  5 , 370 );
   					for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(S[i]*50);
						a=(S[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 420-b , 40 + 10*(i+1) , 420-b );
                                                g2d.drawLine(40+10*(i+1) , 420-b , 40 + 10*(i+1) , 420-a );
						}
						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 420-b , 40 + 10*(i+1) , 420-b );
                                                g2d.drawLine(40+10*(i+1) , 420-b , 40 + 10*(i+1) , 420-a );
						}
						else
                                                g2d.drawLine(40+10*i , 420-(S[i]*50) , 40 + 10*(i+1) , 420-(S[i+1]*50) );
                                        }*/

					

                                        g2d.setColor(Color.black);
                                        g2d.setStroke(new BasicStroke(2));
                                        g2d.drawLine(40 , 20 , 40  , 700 );
                                        g2d.drawLine( 0  , 440 , 700 , 440 );

                                        g2d.drawString("Time --> ",  160 , 460 );

                                        g2d.drawString("WAVEFORM OUTPUT ",  80 , 20 );
                                        g2d.drawString("OF THE CIRCUIT - ",  100 , 40 );
					System.out.println("count " + count);

                                }


               }
		 public class graph extends JPanel   implements MouseMotionListener,MouseListener
                {
                        String fileToRead = "outfile";

                        StringBuffer strBuff;
                        TextArea txtArea;
                        String myline;
                        JLabel l ;

			 int xpos,ypos;
                        int[] A = new int[64] ;
                        int[] B = new int[64] ;
                        int[] C_in = new int[64] ;
                        int[] C_out = new int[64] ;
                        int[] S = new int[64] ;
                        

                        int no_values = 0 ;
                        //public  graph()
                        public  graph ()
                        {
				 int i;
                                System.out.println("xpos ");
                                addMouseMotionListener(this);
                                addMouseListener(this);
                                for(i=0;i<63;i++)
                                {
                                         if(i%4==0 || i % 4==1)
                                                A[i]=1;
                                        else
                                                A[i]=0;
                                        if(i%8<4)
                                                B[i]=1;
                                        else
                                                B[i]=0;
                                        if(i%16<8)
                                                C_in[i]=1;
                                        else
                                                C_in[i]=0;
                                        if(i%16<3 || (i%16 > 8 && i %16 <  11))
                                                S[i]=1;
                                        else
                                                S[i]=0;
                                        if(i%16<3 ||(i%8>6 && i%16 < 8) ||(i%16>10 && i%16 < 15))
                                                C_out[i]=1;
                                        else
                                                C_out[i]=0;
                                }

                        }
			public void make_grap()
			{
			}
			   public void mouseMoved(MouseEvent me)
                        {
                                xpos = me.getX();
                                ypos = me.getY();
                        }
                        public void mouseReleased (MouseEvent me) {
                        }
                        public void mouseEntered (MouseEvent me) {
                        }
                        public void mouseExited (MouseEvent me) {
                        }
                        public void mouseDragged (MouseEvent me) {
                        }
                        public void mousePressed (MouseEvent me) {
                        }
			    public void mouseClicked(MouseEvent me)
                        {
                                int k;
                                xpos = me.getX();
                                ypos = me.getY();
				if(ypos>115 && ypos<140)
				{
					int u;
					int t;
					if(xpos-120<=0)
					{
					t=0;
					}
					else
					{
					u=(xpos-120)/80;
					t=8*(u+1);
					}
						for(k=t;k<t+8;k++)
							C_in[k]=0;
				}
				if(ypos<115 && ypos>90)
				{
					int u;
					int t;
					if(xpos-120<=0)
					{
					t=0;
					}
					else
					{
					u=(xpos-120)/80;
					t=8*(u+1);
					}
						for(k=t;k<t+8;k++)
							C_in[k]=1;
				}
				if(ypos > 160 && ypos < 185)
				{
					System.out.println("reached");
					int u;
					int t;
					if(xpos-80<=0)
					{
					t=0;
					}
					else
					{
					u=(xpos-80)/40;
					t=4*(u+1);
					}
						for(k=t;k<t+4;k++)
							B[k]=1;
					
				}
				if(ypos < 210 && ypos > 185)
				{
					int u;
					int t;
					if(xpos-80<=0)
					{
					t=0;
					}
					else
					{
					u=(xpos-80)/40;
					t=4*(u+1);
					}
						for(k=t;k<t+4;k++)
							B[k]=0;
				}
				if(ypos < 280 && ypos > 255)
				{
					int u;
					int t;
					if(xpos-60<=0)
					{
					t=0;
					}
					else
					{
					u=(xpos-60)/20;
					t=2*(u+1);
					}
						for(k=t;k<t+2;k++)
							A[k]=0;
				}
				if(ypos < 255 && ypos > 230)
				{
					int u;
					int t;
					if(xpos-60<=0)
					{
					t=0;
					}
					else
					{
					u=(xpos-60)/20;
					t=2*(u+1);
					}
						for(k=t;k<t+2;k++)
							A[k]=1;
				}

				repaint();
                                System.out.println("xpos"+xpos);
                                System.out.println("ypos"+ypos);
                        }
			 public void make_graph(String file)
                        {

                                        repaint();


                        }
				   public void paint(Graphics g)
                                {


                                        System.out.println("Hi I am in the gpaint func of the exp1_graph class :)");
                                        int i , j ;
                                        Graphics2D g2d = (Graphics2D)g ;
                                        g2d.setStroke(new BasicStroke(2));
                                        // back ground 
                                        //g2d.setColor(new Color(204 , 255 , 255));
                                        g2d.setColor(Color.lightGray);
                                        g2d.fillRect(0,0,1000,1500);
                                        g2d.setColor(Color.white);
                                        for ( i = 0 ; i < 1500 ; i += 15 )
                                        {
                                                for (j = 0 ; j < 1500 ; j +=5 )
                                                {
                                                        g2d.fillOval(i , j , 1 , 2);
                                                }
                                        }
                                        for ( i = 0 ; i < 1500 ; i += 5 )
                                        {
                                                for (j = 0 ; j < 1500 ; j +=15 )
                                                {
                                                        g2d.fillOval(i , j , 2 , 1);
                                                }
                                        }
					for(i=0;i<64;i++)
					{
						if(A[i]==1 && B[i]==0 && C_in[i]==0 )
						{
							S[i]=1;
						}
						else if(A[i]==0 && B[i]==1 && C_in[i]==0 )
						{
							S[i]=1;
						}
						else if(A[i]==0 && B[i]==0 && C_in[i]==1 )
						{
							S[i]=1;
						}
						else if(A[i]==1 && B[i]==1 && C_in[i]==1 )
						{
							S[i]=1;
						}
						else
							S[i]=0;
						
					}
					for(i=0;i<64;i++)
					{
						if(A[i]==1 && B[i]==1 && C_in[i]==0 )
						{
							C_out[i]=1;
						}
						else if(A[i]==0 && B[i]==1 && C_in[i]==1 )
						{
							C_out[i]=1;
						}
						else if(A[i]==1 && B[i]==0 && C_in[i]==1 )
						{
							C_out[i]=1;
						}
						else if(A[i]==1 && B[i]==1 && C_in[i]==1 )
						{
							C_out[i]=1;
						}
						else
							C_out[i]=0;
						
					}




					         g2d.setColor(Color.red);
					int b;
					int a;
                                        g2d.drawString("CIn ",  5 , 90 );
                                        for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(int)Math.round(C_in[i]*50);
						a=(int)Math.round(C_in[i+1]*50);
						if(b- a > 40)
						{
                                                g2d.drawLine(40+10*i , 140-(int)Math.round(C_in[i]*50) , 40 + 10*(i+1) , 140-b );
                                                g2d.drawLine(40+10*(i+1) , 140-b , 40 + 10*(i+1) , 140-a );
						}
						else if(a-b>40)
						{
                                                g2d.drawLine(40+10*i , 140-b , 40 + 10*(i+1) , 140-b );
                                                g2d.drawLine(40+10*(i+1) , 140-b , 40 + 10*(i+1) , 140-a );
						}
						else	
                                                g2d.drawLine(40+10*i , 140-(int)Math.round(C_in[i]*50) , 40 + 10*(i+1) , 140-(int)Math.round(C_in[i+1]*50) );
                                        }
                                        g2d.drawString("B",  5 , 160 );
                                        for( i = 0 ; i < 63 ; i++ )
                                        {
						if(i<4)
							System.out.println(B[i]);
						b=(int)Math.round(B[i]*50);
						a=(int)Math.round(B[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 210-b , 40 + 10*(i+1) , 210-b );
                                                g2d.drawLine(40+10*(i+1) , 210-b , 40 + 10*(i+1) , 210-a );
						}
						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 210-b , 40 + 10*(i+1) , 210-b );
                                                g2d.drawLine(40+10*(i+1) , 210-b , 40 + 10*(i+1) , 210-a );
						}
						else
                                                g2d.drawLine(40+10*i , 210-(int)Math.round(B[i]*50) , 40 + 10*(i+1) , 210-(int)Math.round(B[i+1]*50) );
                                        }
                                        g2d.drawString("A",  5 , 230 );
                                       for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(int)Math.round(A[i]*50);
						a=(int)Math.round(A[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 280-b , 40 + 10*(i+1) , 280-b );
                                                g2d.drawLine(40+10*(i+1) , 280-b , 40 + 10*(i+1) , 280-a );
						}

						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 280-b , 40 + 10*(i+1) , 280-b );
                                                g2d.drawLine(40+10*(i+1) , 280-b , 40 + 10*(i+1) , 280-a );
						}
						else
                                                g2d.drawLine(40+10*i , 280-(int)Math.round(A[i]*50) , 40 + 10*(i+1) , 280-(int)Math.round(A[i+1]*50) );
                                        }
                                        g2d.setColor(Color.blue);
                                        g2d.drawString("COut ",  5 , 300 );
                                        for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(int)Math.round(C_out[i]*50);
						a=(int)Math.round(C_out[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 350-b , 40 + 10*(i+1) , 350-b );
                                                g2d.drawLine(40+10*(i+1) , 350-b , 40 + 10*(i+1) , 350-a );
						}
						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 350-b , 40 + 10*(i+1) , 350-b );
                                                g2d.drawLine(40+10*(i+1) , 350-b , 40 + 10*(i+1) , 350-a );
						}
						else
                                                g2d.drawLine(40+10*i , 350-(int)Math.round(C_out[i]*50) , 40 + 10*(i+1) , 350-(int)Math.round(C_out[i+1]*50) );
                                        }
                                        g2d.drawString("Sum ",  5 , 390 );
   					for( i = 0 ; i < 63 ; i++ )
                                        {
						b=(int)Math.round(S[i]*50);
						a=(int)Math.round(S[i+1]*50);
						if(b-a > 40)
						{
                                                g2d.drawLine(40+10*i , 420-b , 40 + 10*(i+1) , 420-b );
                                                g2d.drawLine(40+10*(i+1) , 420-b , 40 + 10*(i+1) , 420-a );
						}
						 else if(a-b > 40)
						{
                                                g2d.drawLine(40+10*i , 420-b , 40 + 10*(i+1) , 420-b );
                                                g2d.drawLine(40+10*(i+1) , 420-b , 40 + 10*(i+1) , 420-a );
						}
						else
                                                g2d.drawLine(40+10*i , 420-(int)Math.round(S[i]*50) , 40 + 10*(i+1) , 420-(int)Math.round(S[i+1]*50) );
                                        }

					

                                        g2d.setColor(Color.black);
                                        g2d.setStroke(new BasicStroke(1));
                                        g2d.drawLine(40 , 20 , 40  , 480 );
                                        g2d.drawLine( 0  , 440 , 650 , 440 );

                                        g2d.drawString("Time --> ",  160 , 460 );
                                  //      g2d.drawString("Volt",  10 , 160 );
                                    //    g2d.drawString("Volt",  10 , 360 );

                                        g2d.drawString("WAVEFORM OUTPUT SIMULATION  ",  80 , 20 );
                                        g2d.drawString("OF THE DRAWN CIRCUIT - ",  100 , 40 );

                                //      g2d.drawLine(20 , 260 , 20  , 420 );
                                //      g2d.drawLine( 20  , 420 , 400 , 420 );
                                //      g2d.drawLine(95 , 290 , 1000  , 290 );

                                }


               }




		   public class line
                  {
                          int x1 , y1 , x2 , y2 ; // end and start point of wire 
                          int x[] = new int[200];
                          int y[] = new int[200];
                          int end_index ;
                          boolean del ;
                          public line (int a, int b , int c , int d)
                          {
                                  x1 = a ;
                                  y1 = b ;
                                  x2 = c ;
                                  y2 = d ;
                                  del = false ;
                                  x[0] = a ; x[1] = c ;
                                  y[0] = b ; y[1] = d ;
                                  end_index = 1 ;
                          }
                          public void update2( int c , int d ) // update end point
                          {
                                  x2 = c ;
                                  y2 = d ;
  //                              end_index++;
                                  x[end_index] = c;
                                  y[end_index] = d;
                          }
                          public void update1( int c , int d ) // update start point 
                          {
                                  x[0] = x1 = c ;
                                  y[0] = y1 = d ;
                          }
 			public void update(int a , int b ) // update middle point 
                          {
                                  end_index++;
                                  x[end_index] = a;
                                  y[end_index] = b ;
                          }
                          public void update_wire_mat(int index )
                          {
                                  int i , j , tx1 , ty1 , tx2 , ty2 ; // local vriables 
                                  for ( int k = 0 ; k < end_index ; k ++)
                                  {
                                          tx1 = x[k] ;    tx2 = x[k + 1] ;
                                          ty1 = y[k] ;    ty2 = y[k + 1] ;
                                          for ( i = x1 ;  ;)
                                          {
                                                  if ( tx2 >= tx1 && i >= tx2  ){break;}
                                                  else if( tx2 <= tx1 && i <= tx2 ){break;}
                                                  for ( j = ty1 - 4 ; j < ty1 + 5 ; j ++)
                                                  {
                                                          wire_mat[i][j] =  index ;   // update the matrix as the img is selected  
                                                  }
  
                                                  if ( tx2 > tx1 ){i++;}else{i--;}
                                          }
                                          for ( i = ty1 ;  ;)
                                          {
                                                  if ( ty2 >= ty1 && i >= ty2  ){break;}
                                                  else if( ty2 <= ty1 && i <= ty2 ){break;}
                                                  for ( j = tx2 - 4 ; j < tx2 + 5 ; j ++)
                                                  {
                                                          wire_mat[j][i] =  index ;   // update the matrix as the img is selected  
                                                  }
  
                                                  if ( ty2 > ty1 ){i++;}else{i--;}
                                          }
                                                   }
  
                          }
                          public void update_mat (int index)
                          {
                                  for ( int i = x1 - 4 ; i < x1 +5; i ++ )
                                  {
                                          for ( int j = y1 - 4 ; j < y1 + 5; j ++ )
                                          {
                                                  wire_points_mat[i][j] = index;
                                          }
                                  }
                                  for ( int i = x2 - 4 ; i < x2 +5; i ++ )
                                  {
                                          for ( int j = y2 - 4 ; j < y2 + 5; j ++ )
                                          {
                                                  wire_points_mat[i][j] = index;
                                          }
                                  }
                                  update_wire_mat(index);
                          }
                          public void del()
                          {
                                  update_mat(-1);
                                  del = true ;
                          }
                  }

		public MyPanel()
		{
			super(new BorderLayout());
			try // geting base URL address of this applet 
			{
				base = getDocumentBase();
			}
			catch( Exception e) {}

/*			leftPanel.setLayout(new BorderLayout());
			leftPanel.setMinimumSize(new Dimension(1000,1100)); // for fixing size*/


			leftSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT , toolPanel , workPanel); // spliting left in tool & work
			leftSplitPane.setOneTouchExpandable(true); // this for one touch option 
			leftSplitPane.setDividerLocation(0.2);
//			leftPanel.add(leftSplitPane, BorderLayout.CENTER);
			add(leftSplitPane, BorderLayout.CENTER);
			int i;
			for ( i = 1 ; i <= 4 ; i ++ )
			{
				if(i==2)
					continue;
				java.net.URL imgURL = getClass().getResource("comp" + i + ".gif");
				if (imgURL != null)
				{
					icon[i] =  new ImageIcon(imgURL);
					img[i] =  getImage(imgURL);
				}
				else
				{
					System.err.println("Couldn't find file: " );
					icon[i] =  null;
				}


				img_button1[i] = new JButton ( icon[i] );
				img_button1[i].setOpaque(true);
				img_button1[i].setMargin(new Insets (0,0,0,0));
				img_button1[i].addActionListener(this);
				img_button1[i].setBackground(Color.white);
				img_button1[i].setToolTipText(comp_str[i]);// setting name for hovering of mouse

				leftTool1.add(img_button1[i]);
			}
			int j = 0 ;
			for ( i = 4 ; i < 5 ; i ++ )
			{
				j = 6 + i ; // for index setting 
				java.net.URL imgURL = getClass().getResource("comp" + j + ".gif");
				if (imgURL != null)
				{
					icon[j] =  new ImageIcon(imgURL);
					img[j] =  getImage(imgURL);
				}
				else
				{
					System.err.println("Couldn't find file: " );
					icon[j] =  null;
				}



				img_button2[i] = new JButton ( icon[j] );
				img_button2[i].setOpaque(true);
				img_button2[i].setMargin(new Insets (0,0,0,0));
				img_button2[i].addActionListener(this);
				img_button2[i].setBackground(Color.white);
				img_button2[i].setToolTipText(comp_str[j]); // setting name at hovering of mouse 

				leftTool2.add(img_button2[i]);
			}
			toolPanel.setLayout(new BorderLayout());


			//                      MySelected toolPanelUp = new MySelected();
			toolPanelUp = new JPanel();
			toolPanelDown = new JPanel();
			URL selected_URL = getClass().getResource("comp" + 0 + ".gif");
			if (selected_URL != null)
			{
				icon[0] =  new ImageIcon(selected_URL);
			}
			else
			{
				System.err.println("Couldn't find file: " );
				icon[0] =  null;
			}
			selected = new JButton(icon[0]);

			selected.setBackground(Color.white);
			// selected.setToolTipText(comp_str[0]); // setting name at hovering of mouse 

			toolPanel.add(toolPanelUp , BorderLayout.CENTER);
			toolPanel.add(toolPanelDown , BorderLayout.SOUTH);
			selected.setBorder(BorderFactory.createTitledBorder(" SELECTED ICON "));
			toolPanelDown.setBorder(BorderFactory.createTitledBorder(" AVALIABLE ICONS "));


			toolPanelUp.setLayout(new BorderLayout());
			toolPanelUp.add(selected, BorderLayout.NORTH);
			toolPanelUp.add(new JLabel("<html> <br/><html/>"), BorderLayout.SOUTH);

			toolPanelDown.add(leftTool1);
			toolPanelDown.add(leftTool2);

			leftTool1.setFloatable(false);
			leftTool2.setFloatable(false);
			/*rightPanel.setLayout(new BorderLayout()); 
			rightPanel.setBackground(Color.gray); 

			JLabel wave_head = new JLabel ( "<html><FONT COLOR=white SIZE=6 ><B>SIMULATION OF CIRCUIT</B></FONT><br><br></html>", JLabel.CENTER);
                        wave_head.setBorder(BorderFactory.createRaisedBevelBorder( ));


			waveRightPanel = new graph() ;// = new exp1_graph();
                        rightPanel.add(waveRightPanel, BorderLayout.CENTER);
                        rightPanel.add(wave_head, BorderLayout.NORTH);



			splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT , leftPanel , rightPanel);
			splitPane.setOneTouchExpandable(true); // this for one touch option 
			splitPane.setDividerLocation(0.2);
			add(splitPane, BorderLayout.CENTER);*/


			  add(topPanel , BorderLayout.NORTH);
                        topPanel.setBackground(Color.gray);
                        JPanel headButton = new JPanel (new FlowLayout(FlowLayout.CENTER , 100 , 10 )) ;
                        JLabel heading = new JLabel (  "<html><FONT COLOR=WHITE SIZE=18 ><B>SEMI CUSTOM DESIGN </B></FONT></html>", JLabel.CENTER);
                        heading.setBorder(BorderFactory.createEtchedBorder( Color.black , Color.white));
                       

			  topPanel.setLayout(new BorderLayout());

                        topPanel.add(heading , BorderLayout.CENTER);
                        topPanel.add(headButton , BorderLayout.SOUTH);
                        java.net.URL imgURL = getClass().getResource("simulate1.png");
                        java.net.URL imgURL2 = getClass().getResource("graph.gif");
                        if (imgURL != null && imgURL2 != null)
                        {
                                icon_simulate =  new ImageIcon(imgURL);
                                icon_graph =  new ImageIcon(imgURL2);
                        }
                        else
                        {
                                System.err.println("Couldn't find file: " );
                                icon_simulate =  null;
                                icon_graph =  null;
                        }
                        simulate_button = new JButton("Schematic Diagram" , icon_simulate );
                        icon_simulate.setImageObserver(simulate_button);

                        graph_button = new JButton (" Simulation" , icon_graph);
                        icon_graph.setImageObserver(graph_button);
                        simulate_button.setToolTipText("For Simulation");


			    headButton.add(simulate_button );
                        headButton.add(graph_button );
                   //     headButton.add(exp_list );
                     //   headButton.add(layout_button );


                        simulate_button.addActionListener(this);
                        graph_button.addActionListener(this);
                       // exp_list.addActionListener(this);
                        //layout_button.addActionListener(this);
                        //-------------------------------------------------------------------------------------------
                        //Setting Bottom Panel ========================================================================== 
                        JPanel bottom = new JPanel(new FlowLayout());
                        add(bottom , BorderLayout.SOUTH);
                        JLabel h = new JLabel (  "<html><FONT COLOR=WHITE SIZE=12 ><B>THIS IS VLSI EXPERIMENT</B></FONT></html>", JLabel.CENTER);
//                      bottom.add(h );

                        //================================================================================================
                        setBorder(BorderFactory.createLineBorder( Color.black));
                  

		}
	         	
		public void change_selected (int no)
		{
			selected.setIcon(icon[no]);
		}
		 public boolean circuit_check()
                 {
		
                         int check_points_value[][] = new int[16][1000] ; // each index will store corresponding  component for points of (comp point no -> index )
                         int i = 0 , j , j1 = 0, k1 = 0 , j2 = 0 , k2 = 0 , l = 0 ;
                         for (  i = 0 ; i < 16 ; i ++ )
                         {
                                 for (  j = 0 ; j < 1000 ; j ++ )
                                 {
                                         check_points_value[i][j] = -1 ;
                                 }
                         }
                         System.out.println(total_wire);
                         for (  i = 0 ; i < total_wire ; i ++ )
                         {
                                 j1 = wire[i].x1;
                                 k1 = wire[i].y1;
                                 j2 = wire[i].x2;
                                 k2 = wire[i].y2;
 
			         if (wire[i].del == false &&  end_points_mat[j1][k1] != -1 && end_points_mat[j2][k2] != -1 )
                                 {
 
                                         l = 0 ;
                                         while ( check_points_value[ end_points_mat[j1][k1] ][l] != -1 )
                                         {
                                                 l++;
                                         }
                                         check_points_value[end_points_mat[j1][k1]][l] =  end_points_mat[j2][k2];
 
                                         l = 0 ;
                                         while ( check_points_value[ end_points_mat[j2][k2] ][l] != -1 )
                                         {
                                                 l++;
                                         }
                                         check_points_value[end_points_mat[j2][k2]][l] =  end_points_mat[j1][k1];
                                 }
 
 
                         }
			int temp ;
                         for (  i = 0 ; i < 16 ; i ++ ) // for making aal connected point for each end_point in its array ..
                         {
 
                                 l = 0 ;
                                 while ( check_points_value[i][l] != -1 )
                                 {
                                         l++;
                                 }
 
                                 int r = 0 ;
                                 temp = check_points_value[i][r++];
                                 while ( temp != - 1 )
                                 {
                                         int k = 0 ;
                                         while ( check_points_value[temp][k] != - 1)
                                         {
                                                 int flag = 0 ;
                                                 for ( int m = 0 ; m < l ; m ++ )
                                                 {
                                                         if ( check_points_value[temp][k] == check_points_value[i][m] )
                                                         {
                                                                 flag = 1 ;
                                                                 break ;
                                                         }
                                                 }
                                                 if ( flag == 0 )
                                                 {
                                                         check_points_value[i][l++] = check_points_value[temp][k];
                                                 }
                                                 k++;
                                         }
                                        temp = check_points_value[i][r++]; // for each (element) value of i(th) element of array
                                 }
                         }
                         // checking 
                         for (  i = 0 ; i < 16 ; i ++ )
                         {
                                 l = 1 ;
                                        System.out.println("point is" +i+check_points_value[i][0]);
                         }
	//		if ( exp_type == 0 ) // Complementary 
                        // {
                                 for (  i = 0 ; i < 16 ; i ++ )
                                 {
                                         if ( check_points_value[i][0] == -1 )
                                         {
                                                 return false ; // if any end point of component is free then wrong circiut ...
                                         }
                                 }
				int local_endpoint,local_count_input=0,local_count_output=0;
				for(i=0;i < 4;i++)
				{
					 l = 0 ;
					local_endpoint = check_points_value[i][l];
                                if(local_endpoint == 11 ||local_endpoint == 12 || local_endpoint == 13)
                                {
					local_count_input++;
                                }
                                if(local_endpoint == 14 || local_endpoint == 15)
                                {
					local_count_output++;
                                }
				}
				System.out.println("input_count"+local_count_input);
				System.out.println("input_output"+local_count_output);
				if((local_count_input != 1 && local_count_input != 2) ||((local_count_input + local_count_output) != 2))
					return false;
				for(i=4;i < 8;i++)
				{
					 l = 0 ;
					local_endpoint = check_points_value[i][l];
                                if(local_endpoint == 11 ||local_endpoint == 12 || local_endpoint == 13)
                                {
					local_count_input++;
                                }
                                if(local_endpoint == 14 || local_endpoint == 15)
                                {
					local_count_output++;
                                }
				}
				if(local_count_input != 3 || local_count_output != 1)
					return false;
				local_count_input=0;local_count_output=0;
				for(i=8 ;i < 11;i++)
				{
					 l = 0 ;
					local_endpoint = check_points_value[i][l];
                                if(local_endpoint == 11 ||local_endpoint == 12 || local_endpoint == 13)
                                {
					local_count_input++;
                                }
                                if(local_endpoint == 14 || local_endpoint == 15)
                                {
					local_count_output++;
                                }
				}
				if(local_count_input != 0 || local_count_output != 1)
					return false;
			return true;
			}
		public void actionPerformed(ActionEvent e)
		{
			
			  if(e.getSource() == simulate_button )
			{
				//System.out.println("Simulated1");
				
				//draw_circuit(g2d , 0  , 0, 15 , 0);
				if(simulate_button_pressed == -1)
				{
				if(circuit_check())
				{
					simulate_button_pressed  =  1;
					img_button_pressed = -1;
				System.out.println("Simulated");
				workPanel.repaint();
                        	simulate_button.setText("Start Again");
				 // waveRightPanel.make_graph("txt/outfile");// Read file OUTFILE and draw the 
                               // waveRightPanel.repaint();
                               // waveRightPanel.setVisible(true);
                               // simulate_flag = true ;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Circuit is not Complete , Please Complete it and press simulate again :)");
				}
				}
				else
				{
					simulate_button_pressed  =  -1;
					workPanel.repaint();
				/*	workPanel.repaint();
					waveRightPanel = new graph() ;
				  waveRightPanel.make_graph("txt/null.txt");// Read file OUTFILE and draw the 
                                waveRightPanel.repaint();
                                waveRightPanel.setVisible(true);*/
                                	simulate_flag = false;
                        	simulate_button.setText("Schematic Diagram");
				}
					

			}
			  if(e.getSource() ==  graph_button )
			  {
				if(simulate_button_pressed == 1)
				{
                                        myDialog graphDialog;
					graphDialog = new myDialog( new JFrame() ,"",-1, -1);
					graphDialog.setVisible(true);
					
						
				/*	 Container cp;
					cp = getContentPane();
					BorderLayout layout = new BorderLayout();
					cp.setLayout(layout);
					setSize(700 , 700);
					waveRightPanel = new graph() ;


				  waveRightPanel.make_graph("txt/outfile");// Read file OUTFILE and draw the 
                                	waveRightPanel.repaint();
                                	waveRightPanel.setVisible(true);
					cp.add(waveRightPanel,layout.CENTER);
                               		simulate_flag = true ;*/
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Please press Schematic Diagram Button first and then press simulate again :)");
				}
			  }

			if(simulate_button_pressed != 1)
			{

			 if (e.getSource() == img_button1[1] )
			{
					img_button_pressed = 1 ;
					change_selected(1);
			}
			else if (e.getSource() == img_button1[2] )
			{
					img_button_pressed = 2 ;
					change_selected(2);
			}
			else if (e.getSource() == img_button1[3] ) // Wire :)
			{
				img_button_pressed = 3 ;
				change_selected(3);
			}
			else if (e.getSource() == img_button1[4] ) // Input 
			{
					img_button_pressed = 4 ;
					change_selected(4);
			}
			else if (e.getSource() == img_button2[4] ) //output
			{
					img_button_pressed = 10 ;
					change_selected(10);
			}
			}

		}
		public  class node
		{
			int node_x ;
			int node_y ;
			int img_no ;
			int width ;
			int height ;

			int virtual_w ;
			int virtual_h ;
			int node_number;
			double angle ;
			int angle_count ; // 0-> 0 / 360 degree , 1 -> 90 degree , 2 -> 180 degree , 3 -> 270 degree

			boolean del ;

			// for connection with wire 
			int end_pointsX[] = new int[5];
			int end_pointsY[] = new int[5];
			int count_end_points = 0 ;

			public node (int x , int y , int no , int w , int h,int number)
			{
				node_x = x ;
				node_y = y ;
				if(no==1)
				{
					int i;
					if(xor_delete_count!=0)
					{
					for(i=0;i<xor_delete_count;i++)
					{
						if(xor_delete[i]==-1)
							continue;
						node_number=xor_delete[i];
						xor_delete[i]=-1;
						break;
					}
					}
					else
					{
						node_number=number;
					}
				}
				else if(no==4)
				{
					int i;
					if(input_delete_count!=0)
					{
					for(i=0;i<input_delete_count;i++)
					{
						if(input_delete[i]==-1)
							continue;
						node_number= input_delete[i];
						input_delete[i]=-1;
						break;
					}
					}
					else
					{
						node_number=number;
					}
				}
				else if(no==2)         //For Or(Replace gnd with Or)
				{
					int i;
					if(gnd_delete_count!=0)
					{
					for(i=0;i<gnd_delete_count;i++)
					{
						if(gnd_delete[i]==-1)
							continue;
						node_number=gnd_delete[i];
						gnd_delete[i]=-1;
						break;
					}
					}
					else
					{
					node_number=number;
					}
				}
				else if(no==10)
				{
					int i;
					if(output_delete_count!=0)
					{
					for(i=0;i<output_delete_count;i++)
					{
						if(output_delete[i]==-1)
							continue;
						node_number=output_delete[i];
						output_delete[i]=-1;
						break;
					}
					}
					else
					{
					node_number=number;
					}
				}
				
				img_no = no ;
				del = false ;

				virtual_w = width = w ;
				virtual_h = height = h ;
				angle = 0 ;
				angle_count = 0 ;
				make_end_points(no);
			}
			   public void rotate(int index )
                         {
                                  remove_mat() ;// delete the previous value from work_mat
                  //              angle = angle +  (java.lang.Math.PI/2);
                                  angle_count = (angle_count + 1 )% 4 ;
                                  angle = angle_count *  (java.lang.Math.PI/2);
                                  if ( (angle - 2* java.lang.Math.PI ) > .001 )
                                  {
                                          angle = 0 ;
                                          virtual_w = width ;
                                          virtual_h = height ;
					if(node_x+virtual_w < 0)
					{
							node_x=node_x-virtual_w;
					}
					if(node_y+virtual_h < 0)
					{
							node_y=node_y-virtual_h;
					}
                                  }
                                 if (angle_count == 0 ) // 90 degree
                                  {
                                          virtual_w = width;
                                          virtual_h = height  ;
					if(node_x+virtual_w < 0)
					{
							node_x=node_x-virtual_w;
					}
					if(node_y+virtual_h < 0)
					{
							node_y=node_y-virtual_h;
					}
                                  }
                                  else if (angle_count == 1 ) // 90 degree
                                  {
                                          virtual_w = -height;
                                          virtual_h = width  ;
					if(node_x+virtual_w < 0)
					{
							node_x=node_x-virtual_w;
					}
					if(node_y+virtual_h < 0)
					{
							node_y=node_y-virtual_h;
					}
                                  }
                                  else if (angle_count == 2 ) // 180 degree
                                  {
                                          virtual_w = -width ;
                                          virtual_h = -height ;
					if(node_x+virtual_w < 0)
					{
							node_x=node_x-virtual_w;
					}
					if(node_y+virtual_h < 0)
					{
							node_y=node_y-virtual_h;
					}
						
                                  }
                                  else if (angle_count == 3 ) // 270 degree
                                  {
                                          virtual_w = height ;
                                          virtual_h = -width ;
                                  }
                                  update_mat(index); // update the matrix value to work_mat // index is the index of the node_comp matrix
                          }

			public void remove_mat() // delete the previous value from work_mat
                          {
					int i,j;	
                                          for ( i = node_x ;  ;  )
                                         {
                                                  if ( virtual_w > 0 && i >= node_x + virtual_w  ){break;}
                                                 else if( virtual_w < 0 && i <= node_x + virtual_w  ){break;}
                                                  for ( j = node_y ;  ;  )
                                                  {
                                                          if ( virtual_h > 0 && j >= node_y + virtual_h  ){break;}
                                                          else if( virtual_h < 0 && j <= node_y + virtual_h  ){break;}
  
                                                          work_mat[i][j] =  -1 ;   // update the matrix as the img is selected  
                                                          if ( virtual_h > 0 ){j++;}else{j--;}
                                                  }
                                                  if ( virtual_w > 0 ){i++;}else{i--;}
                                          }
                                  remove_end_points();
				
                          }
                          public void remove_end_points()
                          {
                                  for ( int k = 0 ; k < count_end_points ; k ++ )
                                  {
                                          for ( int i = end_pointsX[k] +8 ; i > end_pointsX[k] -7  &&i > 0 ; i -- )
                                          {
                                                  for ( int j = end_pointsY[k] +8 ; j > end_pointsY[k] -7 && j>0; j -- )
                                                  {
                                                          end_points_mat[i][j] = -1;
                                                  }
                                          }
  
                                  }
                          }

			public void update_end_points_mat(int img)
			{
				if ( img == 1) // Halfadder
				{
					
					for ( int k = 0 ; k < 4 ; k ++ )
					{
						for ( int i = end_pointsX[k] +8 ; i > end_pointsX[k] -7 && i>0; i -- )
						{
							for ( int j = end_pointsY[k] +8 ; j > end_pointsY[k] -7 && j>0; j -- )
							{
								end_points_mat[i][j] = (((node_number)*4))+k ;         //total no. of xor = 2
							}
						}
					}
				}
				 else if ( img == 2)// Or
                                  {
                                          for ( int k = 0 ; k < 3 ; k ++ )
                                          {
                                          for ( int i = end_pointsX[k] - 7 ; i < end_pointsX[k] +8; i ++ )
                                          {
                                                  for ( int j = end_pointsY[k] - 7 ; j < end_pointsY[k] + 8; j ++ )
						  {
							  end_points_mat[i][j] = 8 + k  ; //total number of or = 1
                                                  }
                                          }
                                          }
                                  }

				else if (  img== 4 || img == 10   ) // INPUT , OUTPUT
                                  {
  
                                          for ( int i = end_pointsX[0] - 7 ; i < end_pointsX[0] +8 ; i ++ )
                                          {
                                                  for ( int j = end_pointsY[0] - 7 ; j < end_pointsY[0] +8; j ++ )
                                                  {
                                                          if ( img == 4 ) // input
                                                          {
                                                                  end_points_mat[i][j] =  11 + node_number; //totalnumber of pmos and nmos are assumed to be 2
                                                          }
                                                          else if ( img == 10 ) //output
                                                          {
                                                                  end_points_mat[i][j] =  14 + node_number;
                                                          }
                                                  }
                                          }
                                  }

			}
			public void make_end_points(int img )
			{
				if ( img == 1 || img == 7) // xor/C/NMOS 
				{
					count_end_points = 4;

					int a , b , c , d , e , f ,g ,h;
					a =  -width ; b = -width/4;c = a;d = -b ;e = -a  ;f = b ; g = -a ; h = -b; 
//					a = width ; b= 0 ; c = width ;d = height ; e = 0 ; f = height / 2 ;

					if ( angle_count == 1 )
					{
						a = width/4 ; b= -width ; c = -a ; d = b ; e = a ; f = -b ;g = -a;h = -b;
					}
					else if ( angle_count == 2 )
					{
						a =  width ; b = width/4;c = a;d = -b ;e = -a  ;f = b ; g = -a ; h = -b; 
					}
					else if ( angle_count == 3 )
					{
						a = -width/4 ; b= width ; c = -a ; d = b ; e = a ; f = -b ;g = -a;h = -b;
					}
					end_pointsX[0] = node_x + a ;             //upper left
					end_pointsY[0] = node_y + b;

					end_pointsX[1] = node_x + c ;           //  lower left
					end_pointsY[1] = node_y + d ;

					end_pointsX[2] = node_x + e  ;             //upper right
					end_pointsY[2] = node_y +  f ;
	
					end_pointsX[3] = node_x + g  ;             //lower right
					end_pointsY[3] = node_y +  h ;
				}
				
				else if ( img == 9 ) ///terminal  Not required for this exp.
                                 {
                                         count_end_points = 3;
                                          int a , b ,c,d,e,f ;
					   a = -width ;b = 0; c = -width  ;d = width  ;e=3*width ;f= width / 2;
//                                          a = width / 2 ; b= 0 ;
  
                                          if ( angle_count == 1 )
                                          {
						a = 0;b = -width;c = -width;d = -width;e=-width/2;f = 3*width;
                                          }
                                          else if ( angle_count == 2 )
                                          {
					   a = width ;b = 0; c = width  ;d = -width  ;e=-3*width ;f= -width / 2;
                                          }
                                          else if ( angle_count == 3 )
                                          {
						a = 0;b = width;c = width;d = width;e=width/2;f = -3*width;
                                          }
                                          end_pointsX[0] = node_x + a ;
                                          end_pointsY[0] = node_y + b;
                                          end_pointsX[1] = node_x + c ;
                                          end_pointsY[1] = node_y + d;
                                          end_pointsX[2] = node_x + e ;
                                          end_pointsY[2] = node_y + f;
                                  }
				else if ( img == 2 ) //or
                                 {
                                         count_end_points = 3;
                                          int a , b ,c,d,e,f ;
					   a = 0 ;b = height / 2;c =0 ;d = height/2+height/4;e = height;f = height/2;
					 if ( angle_count == 1 ){
                                                a= -height/2; b = 0; c= -height/2-height/4; d = 0; e = -height/2; f = height;
                                        }
                                        else if ( angle_count == 2 )
                                        {
                                                a = 0 ; b= -height/2 ; c = 0 ; d = -height/2-height/4 ; e = -height ; f = -height / 2 ;
                                        }
                                        else if ( angle_count == 3 )
                                        {
                                                a = height/2 ; b= 0 ; c = height/2+height/4 ; d = 0 ; e = height/2 ; f = -height ;
                                        }

                                     
  
                                          end_pointsX[0] = node_x + a ;
                                          end_pointsY[0] = node_y + b;
                                          end_pointsX[1] = node_x + c ;
                                          end_pointsY[1] = node_y + d;
                                          end_pointsX[2] = node_x + e ;
                                          end_pointsY[2] = node_y + f;
                                  }
				else if ( img == 4 || img == 10 )
                                {
                                        count_end_points = 1 ;
                                        if ( img == 4 )                 // INPUT
                                        {
                                          int a,b;
						a = 2*width;b=0;
                                          if ( angle_count == 1 )
                                          {
						a = 0;b=2*width;
                                          }
                                          else if ( angle_count == 2 )
                                          {
						a = -2*width;b=0;
					 
                                          }
                                          else if ( angle_count == 3 )
                                          {
						a = 0;b= -2*width;
                                          }
                                                end_pointsX[0] = node_x + a;
                                                end_pointsY[0] = node_y  +b ;
                                        }
                                        else     // OUTPUT
                                        {
                                                end_pointsX[0] = node_x  ;
                                                end_pointsY[0] = node_y  ;
                                        }
                                }

				update_end_points_mat(img);
			}
			public void update_mat(int index) // update the matrix value to work_mat // index is the index of the node_comp matrix
                         {
                                  int i , j ;
                                  for ( i = node_x ;  ;)
                                  {
                                                  if ( virtual_w >= 0 && i >= node_x + virtual_w  ){break;}
                                                  else if( virtual_w <= 0 && i <= node_x + virtual_w  ){break;}
  
                                                  for ( j = node_y ;  ;  )
                                                  {
                                                          if ( virtual_h >= 0 && j >= node_y + virtual_h  ){break;}
                                                          else if( virtual_h <= 0 && j <= node_y + virtual_h  ){break;}
  
                                                          work_mat[i][j] =  index ;   // update the matrix as the img is selected  
                                                          if ( virtual_h >= 0 ){j++;}else{j--;}
                                                  }
  
                                                  if ( virtual_w >= 0 ){i++;}else{i--;}
                                  }
				  
				  
                                  make_end_points(img_no);
                          
			}


		}


		 public class myDialog extends JDialog implements ActionListener
                  {
                          JSpinner length ;
                          JSpinner width;
                          JSpinner capacitance;
                          Container cp;
                          JButton del ;
                          JButton ok ;
                          JButton rotate ;
                          int node_index ;
				int image_no;
				   public myDialog (JFrame fr , String comp, int img_no,int node_no)
                        	 {
                                  super (fr , "Graph " , true ); // true to lock the main screen 
					if(img_no == -1)
					{
						cp = getContentPane();
						BorderLayout layout = new BorderLayout();
						cp.setLayout(layout);
						setSize(700 , 700);

		
						waveRightPanel = new graph() ;
						//waveRightPanel.graph();
						waveRightPanel.make_graph("txt/outfile");// Read file OUTFILE and draw the 
                        	        	waveRightPanel.repaint();
                                		waveRightPanel.setVisible(true);
						cp.add(waveRightPanel,layout.CENTER);
        	                       		simulate_flag = true ;
					}
				else
				{
  
                                  node_index = node_no ;
					image_no=img_no;
  
                                  cp = getContentPane();
                                  SpringLayout layout = new SpringLayout();
                                  cp.setLayout(layout);
                                  	setSize(350 , 200);
                                  if ( img_no == 3 || img_no==8 ) // capacitor 
                                  {
                                          SpinnerModel capacitance_model =        new SpinnerNumberModel(5, //initial value
                                                          1, //min
                                                          10, //max
                                                          1);  //step
                                          JLabel comp_name = new JLabel("<html><foet size=4><b>"+comp+"</b></font></html>" );//,icon[icon_no],JLabel.CENTER);
  
                                          layout.putConstraint(SpringLayout.WEST , comp_name , 50,   SpringLayout.WEST , cp );
                                          layout.putConstraint(SpringLayout.NORTH , comp_name , 20,  SpringLayout.NORTH , cp);
  
                                          capacitance = new JSpinner(capacitance_model);
                                          JLabel c = new JLabel("Select the Length of the Wire :");
                                          //JLabel c_unit = new JLabel("m");
  
                                          del = new JButton("Delete Component");
                                          ok = new JButton("O.K");
				//		      rotate = new JButton("Rotate");
  
                                          layout.putConstraint(SpringLayout.WEST , c , 20,   SpringLayout.WEST , cp );
                                          layout.putConstraint(SpringLayout.NORTH , c ,60,  SpringLayout.NORTH , cp);
  
                                          layout.putConstraint(SpringLayout.WEST , capacitance , 20,   SpringLayout.EAST , c );
                                          layout.putConstraint(SpringLayout.NORTH , capacitance , 60,  SpringLayout.NORTH , cp);
  
                                          //layout.putConstraint(SpringLayout.WEST , c_unit , 10,   SpringLayout.EAST , capacitance );
                                          //layout.putConstraint(SpringLayout.NORTH , c_unit , 60,  SpringLayout.NORTH , cp);
  
                                          layout.putConstraint(SpringLayout.WEST , del , 20,   SpringLayout.WEST , cp );
                                          layout.putConstraint(SpringLayout.NORTH , del , 100,  SpringLayout.NORTH , cp);
  
                                  //       layout.putConstraint(SpringLayout.WEST , rotate , 10,   SpringLayout.EAST , del);
                                    //      layout.putConstraint(SpringLayout.NORTH , rotate , 100,  SpringLayout.NORTH , cp);
  
                                          layout.putConstraint(SpringLayout.WEST , ok , 10,   SpringLayout.EAST , del );
                                          layout.putConstraint(SpringLayout.NORTH , ok , 100,  SpringLayout.NORTH , cp);
  
  
                                          cp.add(comp_name);
                                          cp.add(c);
                                          cp.add(capacitance);
                                         // cp.add(c_unit);
                                          cp.add(del);
                                          cp.add(ok);
                                        //  cp.add(rotate);
                                          ok.addActionListener(this);
                                          del.addActionListener(this);
                                         // rotate.addActionListener(this);
                                  }
				else
				{
                                          JLabel comp_name = new JLabel("<html><font size=4><b>"+comp+"</b></font></html>" );//,icon[icon_no],JLabel.CENTER);
					layout.putConstraint(SpringLayout.WEST , comp_name , 50,   SpringLayout.WEST , cp );
                                          layout.putConstraint(SpringLayout.NORTH , comp_name , 20,  SpringLayout.NORTH , cp);

                 //                         capacitance = new JSpinner(capacitance_model);
                   //                       JLabel c = new JLabel("Select the Length of the Wire :");
                                          //JLabel c_unit = new JLabel("m");

                                          del = new JButton("Delete Component");
                                          ok = new JButton("O.K");
                                                      rotate = new JButton("Rotate");

                                         // layout.putConstraint(SpringLayout.WEST , c , 20,   SpringLayout.WEST , cp );
                                         // layout.putConstraint(SpringLayout.NORTH , c ,60,  SpringLayout.NORTH , cp);

                       //                   layout.putConstraint(SpringLayout.WEST , capacitance , 20,   SpringLayout.EAST , c );
                                          //layout.putConstraint(SpringLayout.NORTH , capacitance , 60,  SpringLayout.NORTH , cp);
  
                         //                 layout.putConstraint(SpringLayout.WEST , c_unit , 10,   SpringLayout.EAST , capacitance );
                                          //layout.putConstraint(SpringLayout.NORTH , c_unit , 60,  SpringLayout.NORTH , cp);
  
                                          layout.putConstraint(SpringLayout.WEST , del , 20,   SpringLayout.WEST , cp );
                                          layout.putConstraint(SpringLayout.NORTH , del , 100,  SpringLayout.NORTH , cp);
  
                                         layout.putConstraint(SpringLayout.WEST , rotate , 10,   SpringLayout.EAST , del);
                                          layout.putConstraint(SpringLayout.NORTH , rotate , 100,  SpringLayout.NORTH , cp);
					layout.putConstraint(SpringLayout.WEST , ok , 10,   SpringLayout.EAST , rotate );
                                          layout.putConstraint(SpringLayout.NORTH , ok , 100,  SpringLayout.NORTH , cp);


                                          cp.add(comp_name);
                           //               cp.add(c);
                             //             cp.add(capacitance);
                                         // cp.add(c_unit);
                                          cp.add(del);
                                          cp.add(ok);
                                          cp.add(rotate);
                                          ok.addActionListener(this);
                                          del.addActionListener(this);
                                          rotate.addActionListener(this);


				}
				}
				addWindowListener( new WA());


				}
			 String   get_length()
                          {
                                  return capacitance.getValue().toString();
                          }

			public void actionPerformed(ActionEvent e )
                         {
                                  if(e.getSource() == ok )
                                  {
                                         /* System.out.println("HI ok button is pressed ");
                                          if ( comp_node[node_index].img_no  == 1 ) //PMOS
                                          {
                                                  Pmos_l = get_length();
                                                  Pmos_w = get_width();
                                          }
                                          else if ( comp_node[node_index].img_no  == 7 ) //NMOS
                                          {
                                                  Nmos_l = get_length();
                                                  Nmos_w = get_width();
                                          }*/
                                           if ( image_no  == 3 ) //Capacitor 
                                          {
                                                  hori_len = get_length();
                                          }
                                           else if ( image_no  == 8 ) //Capacitor 
                                          {
                                                  veri_len = get_length();
                                          }
                                          setVisible(false);
                                          workPanel.repaint();
                                  }
				if(e.getSource() == rotate )
                                  {
					
                                          comp_node[node_index].rotate(node_index);
                                          workPanel.repaint();
  //                                      System.out.println(comp_node[node_index].angle);
                                  }

				 if(e.getSource() == del )
                                  {
                                          comp_node[node_index].del = true;
                                          comp_count[comp_node[node_index].img_no] -= 1; // for descrising the count to check no of each comp
                                          int i , j ;
  
                                          comp_node[node_index].remove_mat();
                                  /*      for ( i = comp_node[node_index].node_x ; i < comp_node[node_index].node_x + work_img_height ; i ++ )
 720                                         {
 721                                                 for ( j = comp_node[node_index].node_y ; j < comp_node[node_index].node_y + work_img_width ; j ++ )
 722                                                 {
 723                                                         work_mat[i][j] = -1 ;
 724                                                 }
 725                                         }*/
                                          // updating values of comp in file -------------------------------
                                          if ( comp_node[node_index].img_no  == 1 ) //PMOS
                                          {
                                                  xor_count--;
							xor_delete[xor_delete_count++]=comp_node[node_index].node_number;
                                                  //pmos_count--;
						//	pmos_delete[pmos_delete_count++]=comp_node[node_index].node_number;
                                          }
                                          else if ( comp_node[node_index].img_no  == 7 ) //NMOS
                                          {
                                                  nmos_count--;
							nmos_delete[nmos_delete_count++]=comp_node[node_index].node_number;
                                          }
                                          else if ( comp_node[node_index].img_no  == 2 ) //NMOS
                                          {
                                                  gnd_count--;
							gnd_delete[gnd_delete_count++]=comp_node[node_index].node_number;
					}
                                          else if ( comp_node[node_index].img_no  == 9 ) //NMOS
                                          {
                                                  vdd_count--;
							vdd_delete[vdd_delete_count++]=comp_node[node_index].node_number;
						}
                                          else if ( comp_node[node_index].img_no  == 4 ) //input
                                          {
                                                  input_count--;
							input_delete[input_delete_count++]=comp_node[node_index].node_number;
						}
                                          else if ( comp_node[node_index].img_no  == 10 ) //output
                                          {
                                                  output_count--;
							output_delete[output_delete_count++]=comp_node[node_index].node_number;
						}
                                /*          else if ( comp_node[node_index].img_no  == 8 ) //Capacitor 
                                          {
                                                  Capacitance = null;
                                          }*/
                                          setVisible(false);
  //                                      work_panel_repaint();
                                          workPanel.repaint();
                                  }

			}
		 class WA extends WindowAdapter
                          {
                                  public void windowClosing( WindowEvent ev)
                                  {
                                          setVisible(false);
                                  }
                          }

		}




		public class WorkPanel extends JPanel implements MouseMotionListener,MouseListener
		{
			public WorkPanel()
			{
				 end_points_mat = new int[work_panel_width][work_panel_height];
				 work_mat = new int[work_panel_width][work_panel_height];
				 wire_mat = new int[work_panel_width][work_panel_height];
                                wire_points_mat = new int[work_panel_width][work_panel_height];
                                 
				 int i , j ;
                                  for ( i = 0 ; i < work_panel_width ; i++)
                                  {
                                          for ( j = 0 ; j < work_panel_height ; j++ )
                                          {
						   work_mat[i][j] = -1 ;
                                                end_points_mat[i][j] = -1 ;
                                                wire_mat[i][j] = -1 ;
                                                wire_points_mat[i][j] = -1 ;

                                          }
                                  }

				addMouseMotionListener(this);
				addMouseListener(this);
			}
			public void mouseMoved(MouseEvent me)
			{
				    work_x = me.getX();
                                  work_y = me.getY();
                                  if ( img_button_pressed == 3 && wire_button == 1 )
                                  {
                                  //      System.out.println("in");
                                  //      System.out.println(total_wire);
                                          int x = (work_x % 15)>7 ? (work_x/15)*15+15 : (work_x/15)*15; // for making good 
                                          int y = (work_y % 15)>7 ? (work_y/15)*15+15 : (work_y/15)*15; // accurate wire point around end points 
                                          wire[total_wire-1 ].update2(x , y);
  //                                      wire[total_wire-1 ].update2((work_x/20)*20 , (work_y/20)*20);
                                          repaint();
                                  }


			}
			public void mouseDragged(MouseEvent me)
			{
				int i,j;
                                  work_x = me.getX();
                                  work_y = me.getY();
					if(work_x<15)
						work_x=15;
					if(work_y<15)
						work_y=15;
					for ( i = work_x -30; i < work_x + comp_node[total_comp -1].width +30; i++ )
				   	{
					   for ( j = work_y -30 ; j < work_y + comp_node[total_comp-1].height+30 ; j++ )
					   {
						   if(i <= 45 || j <= 45 ||i >= work_panel_width || j >= work_panel_height || (work_mat[i][j] != -1 && work_mat[i][j] != node_drag))
						   {
							   return;
						   }
					   }
				   	}
				   if (node_drag != -1 )
                                         {
                                                  comp_node[node_drag].remove_mat();
  
                                                  comp_node[node_drag].node_x  = (work_x /15 )*15 ;
                                                  comp_node[node_drag].node_y  = ( work_y /15)*15 ;
  
                                                  comp_node[node_drag].update_mat(node_drag);
                                          }
				   

				repaint();

			}
			public void mouseClicked(MouseEvent m)
			{
				int i,j;
				work_x=m.getX();
				work_y=m.getY();
				if(img_button_pressed==-1)
				{
					myDialog dialog;
						if ( wire_mat[work_x][work_y] != -1 )
                                        {
                                                System.out.println("wire_mat[work_x][work_y]");
                                                System.out.println(wire_mat[work_x][work_y]);
                                                JFrame wire_f = new JFrame();
                                                int n = JOptionPane.showConfirmDialog( wire_f, "Do u want to Delete Wire ?","Wire", JOptionPane.YES_NO_OPTION);
                                                if ( n == 0 )
                                                {
                                                //      System.out.println("Deletded ");
                                                        wire[wire_mat[work_x][work_y]].del();
                                                        repaint();
                                                }
                                                else
                                                {
                                                //      System.out.println("Not Deletded ");
                                                }

                                        }
                                        else if ( work_mat[work_x][work_y]!= -1 ) 
				//	else
					{
					       int temp = work_mat[work_x][work_y] ;
						if(temp!=-1)
						{
                                                  int temp1 = comp_node[temp].img_no ; // temp is no img no 
                                                  if (  temp1 == 1 || temp1 == 2 || temp1 == 3 || temp1 == 7 ||  temp1 == 8 || temp1==9 || temp1 == 4 || temp1 == 10)
                                                  {
                                                          //JOptionPane.showMessageDialog(null, "Eggs are not supposed to be green.");
                                                //          if ( dialog[temp] == null )
                                                  //        {
                                                    //              fr[temp] = new JFrame(); // bec work_mat will store the index of that comp in mat
                                                                  dialog = new myDialog( new JFrame() ,comp_str[temp1] , temp1,temp);
                                                          dialog.setVisible(true);
                                                          }
						}
					}
                                 }

				  else if (img_button_pressed == 3) // i.e line is selected 
                                  {
                                         int x = (work_x % 15)>7 ? (work_x/15)*15+15 : (work_x/15)*15; // for making good 
                                          int y = (work_y % 15)>7 ? (work_y/15)*15+15 : (work_y/15)*15; // accurate wire point around end points 
					System.out.println("X"+work_x);
					System.out.println("y"+work_y);
					System.out.println("wire_button"+wire_button);
					System.out.println("end_point__mat"+end_points_mat[work_x][work_y]);
  
                                          if ( wire_button == 0 ) // button is pressed first time 
                                          {
                                                  //if ( end_points_mat[work_x][work_y] != - 1 ) // if end points r there 
                                                  if ( end_points_mat[x][y] != - 1 ) // if end points r there 
                                                  {
  
                                                  //        wire[total_wire++] = new line((work_x /20)*20, (work_y/20)*20 , (work_x/20)*20 , (work_y/20)*20);
                                                          wire[total_wire++] = new line(x,y , x, y);
                                                          repaint();
                                                          wire_button = 1 ;
                                                  }
                                                  else
                                                  {
                                                       JOptionPane.showMessageDialog(null, "Wire could start OR end at the componet's connection points only ");
                                                  }
                                          }
	                               else
                                          {
                                                 //if ( end_points_mat[work_x][work_y] != - 1 ) // if end points r there 
                                                /* if ( end_points_mat[x][y] != - 1 ) // if end points r there 
                                                 {*/
                                                  //      wire[total_wire - 1].update2((work_x/20)*20 , (work_y/20)*20); // -1 bec inder of first wire is 0 
                                                          wire[total_wire - 1].update2(x , y ); // -1 bec inder of first wire is 0 
                                                          repaint();
  
                                                          wire[total_wire - 1 ].update_mat(total_wire - 1); // 
                                                          img_button_pressed = -1 ;
                                                          change_selected(0);
                                                          wire_button = 0 ;
                                                 /* }
                                                  else
                                                  {       // adding more end points 
                                                          wire[total_wire - 1].update(x , y ); // -1 bec inder of first wire is 0 
                                                          repaint();
                                                  }*/
                                          }
			}            			

				else
				{	
				 if(img_button_pressed!=-1)
				{
					myDialog dialog;
					if(work_x<15)
						work_x=15;
					if(work_y<15)
						work_y=15;
					if(img_button_pressed==1)
					{
						if(xor_count >= 14)
						{
                                                       JOptionPane.showMessageDialog(null, "Circuit Can have maximum of 14 Xor gates ");
						}
						else
						{
				
						comp_node[total_comp] = new node((work_x / 15)*15 , (work_y / 15 )*15 , img_button_pressed , 15 * 4 , 4 * 15,xor_count);
						//	pmos_count++;
							xor_count++;
				comp_node[total_comp].update_mat(total_comp);
				total_comp++;
						}

					}
					else if(img_button_pressed==7)
					{
						if(nmos_count>=4)
						{
                                                       JOptionPane.showMessageDialog(null, "Circuit Can have maximum of 4 NMOS ");
						}
						else
						{
						comp_node[total_comp] = new node((work_x / 15)*15 , (work_y / 15 )*15 , img_button_pressed , 15 * 2 , 4 * 15,nmos_count);
							nmos_count++;
				comp_node[total_comp].update_mat(total_comp);
				total_comp++;
						}

					}
					  else if ( img_button_pressed == 2) // or 
                                         {
						if(gnd_count>=1)
						{
                                                       JOptionPane.showMessageDialog(null, "Circuit Can have maximum of 1 Or Gate ");
						}
						else
						{
                                                 comp_node[total_comp] = new node((work_x / 15)*15 , (work_y / 15 )*15 , img_button_pressed , 15* 4 , 4 * 15,gnd_count);
                                        	 gnd_count++;
				comp_node[total_comp].update_mat(total_comp);
				total_comp++;
						}
					}
					  else if ( img_button_pressed == 4) // input
                                         {
						if(input_count>=8)
						{
                                                       JOptionPane.showMessageDialog(null, "Circuit Can have maximum  8 inputs ");
						}
						else
						{
                                                 comp_node[total_comp] = new node((work_x / 15)*15 , (work_y / 15 )*15 , img_button_pressed , 15*2 , 15*2,input_count);
                                        	 input_count++;
				comp_node[total_comp].update_mat(total_comp);
				total_comp++;
						}
					}
					  else if ( img_button_pressed == 10) // ouput  
                                         {
						if(output_count>=4)
						{
                                                       JOptionPane.showMessageDialog(null, "Circuit Can have maximum 4 outputs ");
						}
						else
						{
                                                 comp_node[total_comp] = new node((work_x / 15)*15 , (work_y / 15 )*15 , img_button_pressed , 15 * 2 , 15*2,output_count);
                                        	 output_count++;
				comp_node[total_comp].update_mat(total_comp);
				total_comp++;
						}
					}
                                         else if (  img_button_pressed == 9 ) // Vdd
                                         {
						if(vdd_count>=1)
						{
                                                       JOptionPane.showMessageDialog(null, "Circuit Can have maximum of 1 VDD ");
						}
						else
						{
                                                 comp_node[total_comp] = new node((work_x / 15)*15 , (work_y / 15 )*15 , img_button_pressed , 15 * 2, 3 * 15,vdd_count);
						vdd_count++;
				comp_node[total_comp].update_mat(total_comp);
				total_comp++;
						}
                                         }
				comp_count[img_button_pressed]++;	
				img_button_pressed=-1;
				change_selected(0);
				repaint();
				}
				}
				
			}
			public void mouseReleased(MouseEvent m)
			{
				 if ( node_drag != -1 )
                                 {
                                         comp_node[node_drag].update_mat(node_drag); // updating the matrix
                                 /*      for ( i = comp_node[node_drag].node_x ; i < comp_node[node_drag].node_x + comp_node[node_drag].width ; i++ )
1141                                         {
1142                                                 for ( j = comp_node[node_drag].node_y ; j < comp_node[node_drag].node_y + comp_node[node_drag].height ; j++ )
1143                                                 {
1144                                                         work_mat[i][j] =  node_drag ;   // update the matrix 
1145                                                 }
1146                                         }*/
                                         node_drag = -1;   // node is unseledted to drag
                                 }

			}
			public void mouseEntered(MouseEvent m)
			{
			}
			public void mouseExited(MouseEvent m)
			{
			}
			public void mousePressed(MouseEvent me)
			{
				int i , j ;
                                 work_x = me.getX();
                                 work_y = me.getY();
				  if ( work_mat[work_x][work_y] != -1 )
                                 {
                                         node_drag = work_mat[work_x][work_y];   // node is selected for drag
 
                                         comp_node[node_drag].remove_mat();                 // update the matrix as the img is selected , so can be moved 
 
				}

			}
			public void paint(Graphics g)
			{
				int i,j;
				String line;
				Graphics2D g2d = (Graphics2D)g;
				g2d.scale(scale_x , scale_y);
				// back ground ----------------
			//	g2d.setColor(Color.black);
				g2d.setColor(Color.white);
				g.fillRect(0,0,work_panel_width+1000 , work_panel_height+1000);
				g2d.setColor(Color.orange);
				g2d.setStroke(new BasicStroke(2));
				for ( i = 0 ; i < work_panel_width +400; i+=15)
				{
					for ( j = 0 ; j < work_panel_height+200 ; j+=15 )
					{
						g2d.drawOval(  i -1,j-1 , 1 , 1);
					}
				}
				if(simulate_button_pressed == 1)
				{
				int local_total_comp = 0,local_x = 0,local_y = 0,local_img_no = 0;
				double local_angle = 0.0;
                                String fileToRead ="txt/circuit.txt";
                                URL url = null;
                                try
                                {
                                        url = new URL(getCodeBase(), fileToRead);
                                }
                                catch(MalformedURLException e){
                                        System.out.println("I did't got the outfile to read :( :( So I am very said ");
                                }
                              //  String line;
                                try{
                                        InputStream in = url.openStream();
                                        BufferedReader dis = new BufferedReader(new InputStreamReader(in));
                                     //   strBuff = new StringBuffer();
                                       // myline = bf.readLine();
			//	File f = new File("http://localhost/RaShip/exp/file.txt");
			//	FileInputStream fis = new FileInputStream(f);
			//	BufferedInputStream bis = new BufferedInputStream(fis);
			//	DataInputStream dis = new DataInputStream(bis);
					
				line = dis.readLine();
				if(line != null)
					local_total_comp  = Integer.parseInt(line);
				System.out.println("Try"+local_total_comp);
				
				for ( i = 0; i < local_total_comp ; i++ )
				{
					
					try
					{
					line = dis.readLine();
					if(line.equals("delete"))
						continue;
					if(line != null)
						local_img_no   = Integer.parseInt(line.trim());
					line = dis.readLine();
					if(line != null)
						local_x   = Integer.parseInt(line.trim());
					line = dis.readLine();
					if(line != null)
						local_y   = Integer.parseInt(line.trim());
					line = dis.readLine();
					if(line != null)
						local_angle   = Double.parseDouble(line.trim());
					}
					catch(IOException e){System.out.println(e);}
					
					/*	if ( local_img_no == 1)
						{
							draw_cmos(g2d , local_x  , local_y , 15 , local_angle);
							g.setColor(Color.yellow);
							g.drawString("PMOS" , local_x , local_y );
						}*/
					/*	else if ( local_img_no == 3)
						{
							draw_horizontal_wire(g2d , local_x  , local_y , comp_node[i].width , comp_node[i].angle);
							g.setColor(Color.yellow);
							//g.drawString(comp_str[1] , comp_node[i].node_x -10 , comp_node[i].node_y + 10 );
						}
						else if ( local_img_no == 8)
						{
							draw_vertical_wire(g2d , comp_node[i].node_x  , comp_node[i].node_y , comp_node[i].height , comp_node[i].angle);
							g.setColor(Color.yellow);
							//g.drawString(comp_str[1] , comp_node[i].node_x -10 , comp_node[i].node_y + 10 );
						}*/
						/*else if ( local_img_no == 7)
						{
							draw_nmos(g2d , local_x  , local_y , 15 , local_angle);
							g.setColor(Color.yellow);
							g.drawString("NMOS" , local_x -10 , local_y + 10 );
						//	g.drawString(comp_str[7] , comp_node[i].node_x + -10 , comp_node[i].node_y + 10 );



						}
						  else if ( local_img_no == 2)
                                                 {
                                                         draw_ground(g2d , local_x  , local_y , 15, local_angle);
                                                       g.setColor(Color.yellow);
							g.drawString("Ground" , local_x + 15 , local_y + 15 );
                                                      // g.drawString(comp_str[2] , comp_node[i].node_x + 20 , comp_node[i].node_y + 20 );
                                                 }
                                                 else if ( local_img_no == 9)
                                                 {
                                                         draw_vdd(g2d , local_x  , local_y , 15, local_angle);
                                                         g.setColor(Color.yellow);
							g.drawString("VDD" , local_x + 30 , local_y + 10 );
                                                        // g.drawString(comp_str[9] , comp_node[i].node_x + 30 , comp_node[i].node_y + 10 );
                                                 }
                                                 else*/ if ( local_img_no == 4)
                                                 {
                                                         draw_input(g2d , local_x  , local_y , 15, local_angle);
                                                        // g.setColor(Color.yellow);
                                                        // g.drawString(comp_str[9] , comp_node[i].node_x + 30 , comp_node[i].node_y + 10 );
                                                 }
                                                 else if ( local_img_no == 10)
                                                 {
                                                         draw_output(g2d , local_x  , local_y , 30, local_angle);
                                                        // g.setColor(Color.yellow);
                                                        // g.drawString(comp_str[9] , comp_node[i].node_x + 30 , comp_node[i].node_y + 10 );
                                                 }

			        }	
				 g2d.setStroke(new BasicStroke(2));
				 int local_total_wire=0,local_end_index=0,local_xk=0,local_yk=0,local_xk1=0,local_yk1=0,local_x1=0,local_y1=0,local_x2=0,local_y2=0;
				 local_total_wire   = Integer.parseInt(dis.readLine());
                                 for ( i = 0 ; i < local_total_wire ; i++ )
                                 {
                                         //if ( wire[i].del == false )
                                         //{
                                         	
                                                g2d.setColor(Color.green);
							try{
							line = dis.readLine();
						        if(line.equals("delete"))
								continue;
							if(line != null)
							{
							local_end_index = new Integer(line).intValue();
//				 			local_end_index   = Integer.parseInt(line.trim());
							}
							}
							catch(IOException e){System.out.println(e);}
                                                 for ( int k = 0 ; k < local_end_index ; k ++ )
                                                 {
							try{
							line = dis.readLine();
							if(line != null)
				 			local_xk   = Integer.parseInt(line.trim());
							line = dis.readLine();
							if(line != null)
				 			local_yk   = Integer.parseInt(line.trim());
							line = dis.readLine();
							if(line != null)
				 			local_xk1   = Integer.parseInt(line.trim());
							line = dis.readLine();
							if(line != null)
				 			local_yk1   = Integer.parseInt(line.trim());
							}
							catch(IOException e){System.out.println(e);}
                                                 //      g2d.drawLine (wire[i].x[k] , wire[i].y[k] , wire[i].x[k+1] , wire[i].y[k+1] );
                                                         g2d.drawLine (local_xk , local_yk , local_xk1 , local_yk );
                                                         g2d.drawLine (local_xk1 , local_yk , local_xk1 , local_yk1 );
                                                 //      g2d.drawLine (wire[i].x1 , wire[i].y1 , wire[i].x2 , wire[i].y1 );
                                                 //      g2d.drawLine (wire[i].x2 , wire[i].y1 , wire[i].x2 , wire[i].y2 );
                                                 }
 
                                                 g2d.setColor(Color.red);
							try
							{
							if((line = dis.readLine()) != null)
				 			local_x1   = Integer.parseInt(line.trim());
							line = dis.readLine();
							if(line != null)
				 			local_y1   = Integer.parseInt(line.trim());
							line = dis.readLine();
							if(line != null)
				 			local_x2   = Integer.parseInt(line.trim());
							line = dis.readLine();
							if(line != null)
				 			local_y2   = Integer.parseInt(line.trim());
							}
							catch(IOException e){System.out.println(e);}
												
                                                 g2d.fillRect (local_x1 -4  , local_y1 -4 , 8 ,8);
                                                 g2d.fillRect (local_x2 - 4 , local_y2 -4 , 8 ,8);
                                         //}
                                 }
				dis.close();
				}
				catch(IOException e){}
					
				}
				else
				{
				for ( i = 0; i < total_comp ; i++ )
				{
					if ( comp_node[i].del != true )
					{
						if ( comp_node[i].img_no == 1)
						{
							draw_xor(g2d , comp_node[i].node_x  , comp_node[i].node_y , 15 , comp_node[i].angle);
							g.setColor(Color.black);
							g.drawString(comp_str[1] , comp_node[i].node_x -10   , comp_node[i].node_y + 10);
						}
						else if ( comp_node[i].img_no == 3)
						{
							draw_horizontal_wire(g2d , comp_node[i].node_x  , comp_node[i].node_y , comp_node[i].width , comp_node[i].angle);
							g.setColor(Color.black);
							//g.drawString(comp_str[1] , comp_node[i].node_x -10 , comp_node[i].node_y + 10 );
						}
			/*			else if ( comp_node[i].img_no == 8)
						{
							draw_vertical_wire(g2d , comp_node[i].node_x  , comp_node[i].node_y , comp_node[i].height , comp_node[i].angle);
							g.setColor(Color.black);
							//g.drawString(comp_str[1] , comp_node[i].node_x -10 , comp_node[i].node_y + 10 );
						}
						else if ( comp_node[i].img_no == 7)
						{
							draw_nmos(g2d , comp_node[i].node_x  , comp_node[i].node_y , 15 , comp_node[i].angle);
							g.setColor(Color.black);
							g.drawString(comp_str[7] , comp_node[i].node_x + -10 , comp_node[i].node_y + 10 );



						}
						  else if ( comp_node[i].img_no == 2)
                                                 {
                                                         draw_or(g2d , comp_node[i].node_x  , comp_node[i].node_y , 15, comp_node[i].angle);
                                                       g.setColor(Color.black);
                                                       //g.drawString("OR" , comp_node[i].node_x  , comp_node[i].node_y + 15 );
                                                 }
                                                 else if ( comp_node[i].img_no == 9)
                                                 {
                                                         draw_vdd(g2d , comp_node[i].node_x  , comp_node[i].node_y , 15, comp_node[i].angle);
                                                         g.setColor(Color.black);
                                                         g.drawString(comp_str[9] , comp_node[i].node_x + 30 , comp_node[i].node_y + 10 );
                                                 }
                          */                       else if ( comp_node[i].img_no == 4)
                                                 {
                                                         draw_input(g2d , comp_node[i].node_x  , comp_node[i].node_y , 30, comp_node[i].angle);
                                                         //g.setColor(Color.yellow);
                                                         //g.drawString(comp_str[9] , comp_node[i].node_x + 30 , comp_node[i].node_y + 10 );
                                                 }
                                                 else if ( comp_node[i].img_no == 10)
                                                 {
                                                         draw_output(g2d , comp_node[i].node_x  , comp_node[i].node_y , 30, comp_node[i].angle);
                                                        // g.setColor(Color.yellow);
                                                        // g.drawString(comp_str[9] , comp_node[i].node_x + 30 , comp_node[i].node_y + 10 );
                                                 }
						else
                                                 {
                                  g2d.drawImage(img[comp_node[i].img_no] , comp_node[i].node_x ,comp_node[i].node_y, work_img_width , work_img_height,  this);
                                                 }


					}
					

				}
				 g2d.setStroke(new BasicStroke(2));
                                 for ( i = 0 ; i < total_wire ; i++ )
                                 {
                                         if ( wire[i].del == false )
                                         {
                                                 g2d.setColor(Color.green);
                                                 for ( int k = 0 ; k < wire[i].end_index ; k ++ )
                                                 {
                                                 //      g2d.drawLine (wire[i].x[k] , wire[i].y[k] , wire[i].x[k+1] , wire[i].y[k+1] );
                                                         g2d.drawLine (wire[i].x[k] , wire[i].y[k] , wire[i].x[k+1] , wire[i].y[k] );
                                                         g2d.drawLine (wire[i].x[k+1] , wire[i].y[k] , wire[i].x[k+1] , wire[i].y[k+1] );
                                                 //      g2d.drawLine (wire[i].x1 , wire[i].y1 , wire[i].x2 , wire[i].y1 );
                                                 //      g2d.drawLine (wire[i].x2 , wire[i].y1 , wire[i].x2 , wire[i].y2 );
                                                 }
 
                                                 g2d.setColor(Color.red);
                                                 g2d.fillRect (wire[i].x1 -4  , wire[i].y1 -4 , 8 ,8);
                                                 g2d.fillRect (wire[i].x2 - 4 , wire[i].y2 -4 , 8 ,8);
                                         }
                                 }
				}

			}
			void draw_horizontal_wire(Graphics2D g , int x , int y , int width , double angle )
			{
                                 g.setColor(Color.yellow);
                                 g.setStroke(new BasicStroke(2));
                                 g.setColor(Color.blue);
                                 g.drawLine(x  , y  , x + width , y );
                                 g.setColor(Color.red);
                                 g.fillRect( x -4, y-4, 8 ,8 );
                                 g.fillRect( x + width -4 , y -4  , 8 ,8 );
			}
			void draw_vertical_wire(Graphics2D g , int x , int y , int height , double angle )
			{
				
                                 g.setColor(Color.yellow);
                                 g.setStroke(new BasicStroke(2));
                                 g.setColor(Color.blue);
                                 g.drawLine(x  , y  , x , y+height );
                                 g.setColor(Color.red);
                                 g.fillRect( x -4, y-4, 8 ,8 );
                                 g.fillRect( x  -4 , y + height -4  , 8 ,8 );
			}
			void draw_circuit(Graphics2D g, int x ,int y ,int height , double angle)
			{
				
                                Graphics2D g2d = (Graphics2D)g ;
			//	draw_pmos(g2d , 15  , 200 , 15 , 0);

				
			}
/*			 void draw_or(Graphics2D g , int x , int y , int width , double angle )
                        {
                                 g.rotate(angle , x , y);
                                 g.setColor(Color.yellow);
                                 g.setStroke(new BasicStroke(2));
                                 g.setColor(Color.blue);
                                 g.drawLine(x,y+2*width,x+width+width/2,y+2*width);
                                 g.drawLine(x,y+3*width,x+width+width/2,y+3*width);
                                 g.drawArc(x+width-width/2,y+2*width-width/2,width,2*width,90,-180);
                               //  g.drawArc(x+width-width/4,y+2*width-width/2,width,2*width,90,-180);
                                 g.drawLine(x+width,y+2*width-width/2,x+2*width,y+2*width-width/2);
                                 g.drawLine(x+width,y+3*width+width/2,x+2*width,y+3*width+width/2);
                                 g.drawArc(x+2*width-width/2,y+2*width-width/2,width,2*width,90,-180);
                                 g.drawLine(x+3*width-width/2,y+2*width+width/2,x+4*width,y+2*width+width/2);
                                 g.drawLine(x+4*width,y+2*width,x+4*width,y+2*width+width/2);
                                 g.setStroke(new BasicStroke(1));
                                 g.setColor(Color.red);
                                 g.fillRect( x - 4, y+2*width  -4, 8 ,8 );
                                 g.fillRect( x - 4, y+3*width  -4, 8 ,8 );
                                 g.fillRect( x +4*width- 4, y+2*width -4, 8 ,8 );
                                 g.rotate(-angle , x , y);

                        }
*/
			void draw_xor(Graphics2D g , int x , int y , int width , double angle)
			{
                                 g.rotate(angle , x , y);
                                 g.setColor(Color.yellow);
                                 g.setStroke(new BasicStroke(2));
                                 g.setColor(Color.blue);
                                 g.drawLine(x,y+2*width,x+width+width/2,y+2*width);
                                 g.drawLine(x,y+3*width,x+width+width/2,y+3*width);
                                 g.drawArc(x+width-width/2,y+2*width-width/2,width,2*width,90,-180);
                                 g.drawArc(x+width-width/2+5,y+2*width-width/2,width,2*width,90,-180);
                               //  g.drawArc(x+width-width/4,y+2*width-width/2,width,2*width,90,-180);
                                 g.drawLine(x+width,y+2*width-width/2,x+2*width,y+2*width-width/2);
                                 g.drawLine(x+width,y+3*width+width/2,x+2*width,y+3*width+width/2);
                                 g.drawArc(x+2*width-width/2,y+2*width-width/2,width,2*width,90,-180);
                                 g.drawLine(x+3*width-width/2,y+2*width+width/2,x+4*width,y+2*width+width/2);
                                 g.drawLine(x+4*width,y+2*width,x+4*width,y+2*width+width/2);
                                 g.setStroke(new BasicStroke(1));
                                 g.setColor(Color.red);
                                 g.fillRect( x - 4, y+2*width  -4, 8 ,8 );
                                 g.fillRect( x - 4, y+3*width  -4, 8 ,8 );
                                 g.fillRect( x +4*width- 4, y+2*width -4, 8 ,8 );
                                 g.rotate(-angle , x , y);
			}
			/*void draw_xor(Graphics2D g , int x , int y , int width , double angle)
			{
			//	g.drawImage(comp2.gif,x,y,observer);
				g.rotate(angle , x , y);
				g.setColor(Color.yellow);
//				g.drawRect( x , y , 2*width , 4*width);

				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				
				g.drawLine(x-width,y,x,y);
				g.drawLine(x-width,y+width,x,y+width);
			//	g.fillRect(x,y-5,20,30);
				g.drawArc(x - 5  ,y - 5 ,2*width - 5 ,width + 10 ,130,-260);
				g.drawArc(x - 15  ,y - 5 ,width/2+5 ,width + 10 ,50,-110);
				g.drawLine(x+2*width - 7,y+(width/2),x+(3*width),y+(width/2));
			//	g.drawLine(x+40,y,x+40,y+10);

				g.setStroke(new BasicStroke(1));
				// end points 
				g.setColor(Color.red);
				//g.drawRect(x,y,8,8);
				g.fillRect(x-4-width,y-4,8,8);
				g.fillRect(x-4-width,y+width-4,8,8);
				g.fillRect(x-4+(3*width),y+(width/2) - 4,8,8);
				
				g.setColor(Color.black);
				g.rotate(-angle , x , y);
			}*/
			/*void draw_xor(Graphics2D g , int x , int y , int width , double angle )
                         {
				
                                 int w = width/2;
                                 g.rotate(angle , x , y);
                                 g.setColor(Color.yellow);
 //                              g.drawRect( x , y , 2*width , 4*width);
 
                                 g.setStroke(new BasicStroke(2));
                                 g.setColor(Color.blue);
				 
				 g.drawLine(x- w, y + w ,x + w , y + w);
				 g.drawLine(x- w, y + w ,x - w , y - w);
				 g.drawLine(x- w, y - w ,x + w , y - w);
				 g.drawLine(x + w, y - w ,x + w , y + w);
				 g.drawLine(x - width, y - width/4 ,x - width/2 , y - width/4);
				 g.drawLine(x - width, y + width/4 ,x - width/2 , y + width/4);
				 g.drawLine(x + width, y + width/4 ,x + width/2 , y + width/4);
				 g.drawLine(x +  width, y - width/4 ,x + width/2 , y - width/4);
                              */ /*  g.drawLine(x  , y + 2*width , x + width , y + 2*width);
 
                                 g.drawLine(x + width , y + width/4 +width, x +  width , y +(7* width)/4 +width);
                                 g.drawLine(x + width +width/4 , y + width/4 +width, x +  width +width/4, y +(7* width)/4 +width);
 
                                 g.drawLine(x + (5*width)/4 , y + width/2+width, x + 2* width , y + width/2+width );
                                 g.drawLine(x + (5*width)/4 , y +(3* width)/2+width, x +  2*width , y +(3* width)/2+width);
 
                                 g.drawLine(x + 2*width , y , x + 2*width , y + (3* width)/2);
                                 g.drawLine(x + 2*width , y + 4*width , x + 2*width , y + (5* width)/2);*/
 
                                /* g.setStroke(new BasicStroke(1));
                                 // end points 
                                 g.setColor(Color.red);
                                 g.fillRect( x - width - 4, y - width/4 - 4 , 8 ,8 );
                                 g.fillRect( x - width - 4, y + width/4 - 4 , 8 ,8 );
                                 g.fillRect( x + width - 4, y - width/4 - 4 , 8 ,8 );
                                 g.fillRect( x + width - 4, y + width/4 - 4 , 8 ,8 );
 
                                 g.setColor(Color.black);
                                 g.rotate(-angle , x , y);
                         }*/
			/*void draw_nmos(Graphics2D g , int x , int y , int width , double angle )
                         {
 
                                 g.rotate(angle , x , y);
                                 g.setColor(Color.yellow);
 //                              g.drawRect( x , y , 2*width , 4*width);
 
                                 g.setStroke(new BasicStroke(2));
                                 g.setColor(Color.blue);
                                 g.drawLine(x  , y + 2*width , x + width , y + 2*width);
 
                                 g.drawLine(x + width , y + width/4 +width, x +  width , y +(7* width)/4 +width);
                                 g.drawLine(x + width +width/4 , y + width/4 +width, x +  width +width/4, y +(7* width)/4 +width);
 
                                 g.drawLine(x + (5*width)/4 , y + width/2+width, x + 2* width , y + width/2+width );
                                 g.drawLine(x + (5*width)/4 , y +(3* width)/2+width, x +  2*width , y +(3* width)/2+width);
 
                                 g.drawLine(x + 2*width , y , x + 2*width , y + (3* width)/2);
                                 g.drawLine(x + 2*width , y + 4*width , x + 2*width , y + (5* width)/2);
 
                                 g.setStroke(new BasicStroke(1));
                                 // end points 
                                 g.setColor(Color.red);
                                 g.fillRect( x - 4, y + 2*width -4, 8 ,8 );
                                 g.fillRect( x + 2*width -4, y - 4 , 8 ,8 );
                                 g.fillRect( x + 2*width -4, y +4 * width - 4 , 8 ,8 );
 
                                 g.setColor(Color.black);
                                 g.rotate(-angle , x , y);
                         }
		void draw_cmos(Graphics2D g , int x , int y , int width , double angle)
                         {
 
                                 g.rotate(angle , x , y);
                                 g.setColor(Color.yellow);
 //                              g.drawRect( x , y , 2*width , 4*width);
 
                                 g.setColor(Color.blue);
                                 g.drawOval( x + width - 6, y + 2*width - 3, 6,6 );
                                 g.setStroke(new BasicStroke(2));
                                 g.drawLine(x  , y + 2*width , x + width - 6,y + 2*width);
 
                                 g.drawLine(x + width , y + width/4 +width, x +  width , y +(7* width)/4 +width);
                                 g.drawLine(x + width +width/4 , y + width/4 +width, x +  width +width/4, y +(7* width)/4 +width);
 
                                 g.drawLine(x + (5*width)/4 , y + width/2+width, x + 2* width , y + width/2+width );
                                 g.drawLine(x + (5*width)/4 , y +(3* width)/2+width, x +  2*width , y +(3* width)/2+width);
 
                                 g.drawLine(x + 2*width , y , x + 2*width , y + (3* width)/2);
                                 g.drawLine(x + 2*width , y + 4*width , x + 2*width , y + (5* width)/2);
 
                                 g.setStroke(new BasicStroke(1));
                                 // end points 
                                 g.setColor(Color.red);
                                 g.fillRect( x - 4, y + 2*width -4, 8 ,8 );
                                 g.fillRect( x + 2*width -4, y - 4 , 8 ,8 );
                                 g.fillRect( x + 2*width -4, y +4 * width - 4 , 8 ,8 );
 
                                 g.setColor(Color.black);
                                 g.rotate(-angle , x , y);
                         }
			 void draw_ground(Graphics2D g , int x , int y , int width , double angle)
                         {
                                 g.rotate(angle , x , y);
                                 g.setColor(Color.yellow);
 //                              g.drawRect( x , y , 2*width , 2*width);
 
                                 g.setColor(Color.blue);
                                 g.setStroke(new BasicStroke(2));
                                 g.drawLine(x +width  , y  , x + width ,y + (5*width)/4);
                                 g.drawLine(x +width/2  , y + (5*width)/4 , x + (3*width)/2 ,y + (5*width)/4);
                                 g.drawLine(x +(3*width)/4  , y + (6*width)/4 , x + (5*width)/4 ,y + (6*width)/4);
                                 g.drawLine(x +(7*width)/8  , y + (7*width)/4 , x + (9*width)/8 ,y + (7*width)/4);
                                 g.setStroke(new BasicStroke(1));
                                 // end points 
                                 g.setColor(Color.red);
                                 g.fillRect( x +width - 4, y  -4, 8 ,8 );
                                 g.rotate(-angle , x , y);
 
                         }
			void draw_vdd(Graphics2D g , int x , int y , int width , double angle)
                         {
                                 g.rotate(angle , x , y);
                                 g.setColor(Color.yellow);
                         //      g.drawRect( x , y , 2*width , 3*width);
 
                                 g.setColor(Color.blue);
                                 g.setStroke(new BasicStroke(2));
                                 g.drawLine(x +width  , y  , x + width ,y + 2*width);
                                 g.setStroke(new BasicStroke(4));
                                 g.drawLine(x  , y +(5*width)/2 , x + (4*width)/2 ,y + (3*width)/2);
                         //      g.drawLine(x +width/2  , y + (5*width)/4 , x + (3*width)/2 ,y + (5*width)/4);
                         //      g.drawLine(x +(3*width)/4  , y + (6*width)/4 , x + (5*width)/4 ,y + (6*width)/4);
                                 g.setStroke(new BasicStroke(1));
                                 // end points 
                                 g.setColor(Color.red);
                                 g.fillRect( x +width - 4, y  -4, 8 ,8 );
                                 g.rotate(-angle , x , y);
                         }*/
			void draw_input(Graphics2D g , int x , int y , int width , double angle)
			{
				g.rotate(angle , x , y);	
				g.setColor(Color.black);
				g.drawString("IN", x + 5 , y+ width - 5);
			//	g.drawRect( x , y , 2 *width , width);

				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				g.drawLine(x  , y  , x + 2*width ,y );
				g.drawLine(x  , y + width  , x + width ,y + width );
				g.drawLine(x  , y , x , y + width );
			//	g.drawLine(x + width , y , x + (3*width)/2 , y + width/2);
				g.drawLine(x + width , y + width , x + (3*width)/2 , y );
			//	g.drawLine(x + 2*width , y + width /2 , x + (3*width)/2 , y + width/2);
			//	g.drawLine(x +width/2  , y + (5*width)/4 , x + (3*width)/2 ,y + (5*width)/4);
			//	g.drawLine(x +(3*width)/4  , y + (6*width)/4 , x + (5*width)/4 ,y + (6*width)/4);
				g.setStroke(new BasicStroke(1));
				// end points 
				g.setColor(Color.red);
				g.fillRect( x + 2*width - 4, y  -4, 8 ,8 );
				g.rotate(-angle , x , y);
			}
			void draw_output(Graphics2D g , int x , int y , int width , double angle)
			{
				
				g.rotate(angle , x , y);
				g.setColor(Color.black);
				g.drawString("OUT", x  + width / 2, y+ width - 5);
			
			//	g.drawRect( x , y , 2 *width , width);

				g.setColor(Color.blue);
				g.setStroke(new BasicStroke(2));
				g.drawLine(x + width/2 , y  , x + (3*width)/2 ,y );
				g.drawLine(x + width/2 , y + width  , x + (3*width)/2 ,y + width );
				g.drawLine(x + width/2  , y , x + width/2 , y + width );

				g.drawLine(x + (3*width)/2 , y , x + 2*width , y + width/2);
				g.drawLine(x + (3*width)/2, y +	 width , x + 2*width , y + width/2);
			
				g.drawLine(x , y  , x + width/2 , y );
			//	g.drawLine(x +width/2  , y + (5*width)/4 , x + (3*width)/2 ,y + (5*width)/4);
			//	g.drawLine(x +(3*width)/4  , y + (6*width)/4 , x + (5*width)/4 ,y + (6*width)/4);
				g.setStroke(new BasicStroke(1));
				// end points 
				g.setColor(Color.red);
				g.fillRect( x  - 4, y -4, 8 ,8 );
				g.rotate(-angle , x , y);
			}





}
}
}
