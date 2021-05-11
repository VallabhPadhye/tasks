/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Dell
 */
public final class MainTask extends javax.swing.JFrame {

   int x,y;
   ImageIcon icon;
   CardLayout cardLayout;
   String t;
   String sdate;
   LocalDate date = LocalDate.now(); // Gets the current date
   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy"); 
   DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d/MMM/yyyy_hh:mm:ss a");
   LocalDateTime localDateTime = LocalDateTime.now();
   String ldtString = formatter2.format(localDateTime);
    public MainTask() {
        initComponents();
        this.setResizable(false);
        this.setSize(984,796);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	this.setLocation(screenSize.width - this.getWidth(), screenSize.height - this.getHeight() - 50);
        icon = new ImageIcon("src/res/appicon.png");
        setIconImage(icon.getImage());
        cardLayout = (CardLayout)(panelcards.getLayout());
        loadctask();
        loadntask(); 
        
        
        //new task panel code lies below
         duedate.getJCalendar().setMinSelectableDate(new Date());// disables past dates
         autoid();   
    }
    //Main task Screen Code Lies Here
     public void loadctask(){ //load task on home if available
        try{ 
        Statement s = dbcon.mycon().createStatement();
        ResultSet rs = s.executeQuery("Select * from current order by taskid asc limit 1");
         if(rs.next()==false){
             cardLayout.show(panelcards,"newtask");
             jButton1.setForeground(new java.awt.Color(255, 255, 255));
             jButton2.setForeground(new java.awt.Color(255, 255, 0));
             jButton3.setForeground(new java.awt.Color(255, 255, 255));
             JOptionPane.showMessageDialog(this,"No tasks scheduled...!\nCreate new Task","Warning",JOptionPane.WARNING_MESSAGE);
         }
         else{
                t= rs.getString("taskid");
                ctp.setText(rs.getString("task"));
                sub.setText(rs.getString("subject"));
                sdate=rs.getString("startdate");
                ctid.setText(rs.getString("taskid"));
                dd.setText(rs.getString("duedate"));
             
                //Button Color Code
                jButton1.setForeground(new java.awt.Color(255, 255, 0));
                jButton2.setForeground(new java.awt.Color(255, 255, 255));
                jButton3.setForeground(new java.awt.Color(255, 255, 255));
            }
    }catch(Exception e){System.out.println(e);}
    }  
     public void loadntask(){  //load upcoming task on home if available
        try{ 
        Statement s = dbcon.mycon().createStatement();
        ResultSet rs = s.executeQuery("Select * from current order by taskid asc limit 1,1");
        if(rs.next()==false){
            utl.setVisible(false);
            utl1.setVisible(false);
            dd2.setVisible(false);
            utp.setVisible(false);
            utl2.setVisible(true);
            
        }
          else
            {
                utp.setText(rs.getString("subject"));
                dd2.setText(rs.getString("duedate"));
                utl2.setVisible(false);
            }
    }catch(Exception e){System.out.println(e);}
    }
     
//New task Screen Code Lies Here   
     //autoid code lies here
     public void autoid()
    {
              Thread clk = new Thread() 
        {
            @Override
            public void run()
            {
                for(;;)
                {           
                  DateFormat dateformat = new SimpleDateFormat("d/MMM/yyyy_hh:mm:ss a");
                  String datestring = dateformat.format(new Date()).toString(); 
                  tempid.setText(datestring);
                    try
                    {
                        sleep(1000);       
                    }catch(Exception e){}
                }
            }
        };
      clk.start();
    }
        
     //history page code lies here
     public void loadhtable() //loads table data
     {
           
        try{
        
            String sql = "select taskid, subject ,start_date, due_date from completed";
            PreparedStatement pst = dbcon.mycon().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            ctable.setModel(DbUtils.resultSetToTableModel(rs));
            }catch(InstantiationException | IllegalAccessException | SQLException e){}
            
            JTableHeader th = ctable.getTableHeader();
            TableColumnModel tcm = th.getColumnModel();
            TableColumn tc = tcm.getColumn(0);
            tc.setHeaderValue( "TASK ID" );
            TableColumn tc1 = tcm.getColumn(1);
            tc1.setHeaderValue( "SUBJECT" );
            TableColumn tc2 = tcm.getColumn(2);
            tc2.setHeaderValue( "CREATED" );
            TableColumn tc3 = tcm.getColumn(3);
            tc3.setHeaderValue( "EXPIRED" );
            th.repaint();
        
             JTableHeader chead = ctable.getTableHeader();
             chead.setForeground(Color.BLACK);
             chead.setFont(new Font("TAHOMA", Font.BOLD,14));
             TableColumn column1 = ctable.getColumnModel().getColumn(0);
             column1.setPreferredWidth(200);
             TableColumn column2 = ctable.getColumnModel().getColumn(1);
             column2.setPreferredWidth(415);
             TableColumn column3 = ctable.getColumnModel().getColumn(2);
             column3.setPreferredWidth(100);
             TableColumn column4 = ctable.getColumnModel().getColumn(3);
             column4.setPreferredWidth(100);

     }
     public void loadtaskid()//loads task id's in combo box
    {
        taskidcombo.addItem("SELECT TASK ID");
        try{
         String sql = "select * from completed";
         PreparedStatement pst = dbcon.mycon().prepareStatement(sql);
         ResultSet rs = pst.executeQuery();
         while(rs.next())
         {   
             taskidcombo.addItem(rs.getString("taskid"));
         }
         }catch(Exception e){} 
    }
    void searchtask() //searches task on selecing id from combobox   
    {
        try{
        String cell = (String)taskidcombo.getSelectedItem();
        String cell2 = "SELECT TASK ID"; 
         
         DefaultTableModel dt =  (DefaultTableModel) ctable.getModel();
         dt.setRowCount(0);
         Statement s = dbcon.mycon().createStatement();       
         if("SELECT TASK ID".equals(cell))
         {
              ResultSet rs = s.executeQuery("select taskid, subject ,start_date, due_date from completed");
         
         while (rs.next()) {             
             
             Vector v =new Vector();
             v.add(rs.getString(1));
             v.add(rs.getString(2));
             v.add(rs.getString(3));
             v.add(rs.getString(4));
             dt.addRow(v);
             
         }}
         else{
         ResultSet rs = s.executeQuery("select taskid, subject ,start_date, due_date from completed WHERE taskid like'%"+cell+"%'");
         
         while (rs.next()) {             
             
             Vector v =new Vector();
             v.add(rs.getString(1));
             v.add(rs.getString(2));
             v.add(rs.getString(3));
             v.add(rs.getString(4));
             dt.addRow(v);
             
         }    
         }
        }catch(Exception e){
            //loadtable();
        }
    }
    public void emptyhistory() // check for empty history
    {
       try{    
        Statement s = dbcon.mycon().createStatement();
        ResultSet rs = s.executeQuery("Select * from completed order by taskid asc limit 1");
         if(rs.next()==false)
         {
            JOptionPane.showMessageDialog(this,"Empty History!");
            cardLayout.show(panelcards,"newtask");
            jButton1.setForeground(new java.awt.Color(255, 255, 255));
            jButton2.setForeground(new java.awt.Color(255, 255, 0));
            jButton3.setForeground(new java.awt.Color(255, 255, 255));
        }
         else{
            cardLayout.show(panelcards,"history"); 
            jButton1.setForeground(new java.awt.Color(255, 255, 255));
            jButton2.setForeground(new java.awt.Color(255, 255, 255));
            jButton3.setForeground(new java.awt.Color(255, 255, 0));
         }
       }catch(InstantiationException | IllegalAccessException | SQLException | HeadlessException e){JOptionPane.showMessageDialog(this,e);}
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        panelcards = new javax.swing.JPanel();
        home = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ctid = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dd = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        sub = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ctp = new javax.swing.JTextPane();
        jButton7 = new javax.swing.JButton();
        utl = new javax.swing.JLabel();
        utp = new javax.swing.JLabel();
        utl1 = new javax.swing.JLabel();
        dd2 = new javax.swing.JLabel();
        utl2 = new javax.swing.JLabel();
        newtask = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tempid = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        duedate = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        subject = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        addtask = new javax.swing.JTextArea();
        jButton8 = new javax.swing.JButton();
        history = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        taskidcombo = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        ctable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 0));
        setMinimumSize(new java.awt.Dimension(968, 782));
        setUndecorated(true);
        getContentPane().setLayout(null);

        jSplitPane1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jSplitPane1.setDividerSize(0);

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));

        jButton1.setBackground(new java.awt.Color(51, 51, 51));
        jButton1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("HOME");
        jButton1.setContentAreaFilled(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(51, 51, 51));
        jButton2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("NEW TASK");
        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusPainted(false);
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(51, 51, 51));
        jButton3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("HISTORY");
        jButton3.setContentAreaFilled(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusPainted(false);
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(297, 297, 297)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(355, 355, 355))
        );

        jSplitPane1.setLeftComponent(jPanel3);

        panelcards.setBackground(new java.awt.Color(102, 102, 255));
        panelcards.setLayout(new java.awt.CardLayout());

        home.setBackground(new java.awt.Color(102, 102, 255));
        home.setLayout(null);

        jPanel4.setBackground(new java.awt.Color(255, 0, 0));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("HOME");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
        );

        home.add(jPanel4);
        jPanel4.setBounds(0, 0, 880, 47);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("CURRENT TASK:");
        home.add(jLabel5);
        jLabel5.setBounds(50, 90, 142, 20);

        ctid.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        ctid.setForeground(new java.awt.Color(255, 255, 0));
        ctid.setText("CURRENT TASK");
        home.add(ctid);
        ctid.setBounds(210, 90, 283, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("DUE DATE:");
        home.add(jLabel6);
        jLabel6.setBounds(562, 87, 104, 20);

        dd.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        dd.setForeground(new java.awt.Color(255, 255, 0));
        dd.setText("DATE");
        home.add(dd);
        dd.setBounds(678, 87, 188, 20);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("SUBJECT:");
        home.add(jLabel8);
        jLabel8.setBounds(50, 140, 79, 20);

        sub.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        sub.setForeground(new java.awt.Color(255, 255, 0));
        sub.setText("SUBJECT APPEARS HERE");
        home.add(sub);
        sub.setBounds(150, 140, 680, 20);

        ctp.setEditable(false);
        ctp.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        ctp.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        ctp.setAlignmentX(2.0F);
        ctp.setAlignmentY(2.0F);
        jScrollPane1.setViewportView(ctp);

        home.add(jScrollPane1);
        jScrollPane1.setBounds(44, 170, 780, 197);

        jButton7.setBackground(new java.awt.Color(255, 0, 0));
        jButton7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/mark_as_read_32.png"))); // NOI18N
        jButton7.setText("MARK AS COMPLETED");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        home.add(jButton7);
        jButton7.setBounds(570, 390, 250, 37);

        utl.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        utl.setForeground(new java.awt.Color(255, 255, 255));
        utl.setText("UPCOMING TASK:");
        home.add(utl);
        utl.setBounds(30, 450, 146, 20);

        utp.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        utp.setText("SUBJECT APPEARS HERE");
        home.add(utp);
        utp.setBounds(190, 450, 596, 20);

        utl1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        utl1.setForeground(new java.awt.Color(255, 255, 255));
        utl1.setText("DUE DATE:");
        home.add(utl1);
        utl1.setBounds(30, 480, 90, 20);

        dd2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        dd2.setText("due date");
        home.add(dd2);
        dd2.setBounds(130, 480, 241, 20);

        utl2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        utl2.setForeground(new java.awt.Color(255, 255, 255));
        utl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        utl2.setText("YAAY! YOU ARE DOING LAST TASK...!");
        utl2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true)));
        home.add(utl2);
        utl2.setBounds(200, 660, 470, 45);

        panelcards.add(home, "home");

        newtask.setBackground(new java.awt.Color(102, 102, 255));

        jPanel5.setBackground(new java.awt.Color(255, 0, 0));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("CREATE TASK");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
        );

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("TASK:");

        tempid.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tempid.setForeground(new java.awt.Color(255, 255, 0));
        tempid.setText("TASKID");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("DUE DATE:");

        duedate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        duedate.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("TASK:");

        subject.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        subject.setToolTipText("Enter subject of task in maximim 50 characters");
        subject.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        subject.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                subjectKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                subjectKeyTyped(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("DESCRIPTION____________________________________________________________________");

        addtask.setColumns(20);
        addtask.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        addtask.setRows(5);
        addtask.setTabSize(4);
        addtask.setToolTipText("");
        addtask.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        jScrollPane2.setViewportView(addtask);

        jButton8.setBackground(new java.awt.Color(255, 0, 0));
        jButton8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add_task_32.png"))); // NOI18N
        jButton8.setText("ADD TASK");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout newtaskLayout = new javax.swing.GroupLayout(newtask);
        newtask.setLayout(newtaskLayout);
        newtaskLayout.setHorizontalGroup(
            newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(newtaskLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(newtaskLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(10, 10, 10)
                                .addComponent(tempid, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(150, 150, 150)
                                .addComponent(jLabel10)
                                .addGap(10, 10, 10)
                                .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(newtaskLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(10, 10, 10)
                                .addComponent(subject, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(0, 38, Short.MAX_VALUE))
        );
        newtaskLayout.setVerticalGroup(
            newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newtaskLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(newtaskLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(duedate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(newtaskLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(tempid)
                            .addComponent(jLabel10))))
                .addGap(20, 20, 20)
                .addGroup(newtaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subject, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(114, Short.MAX_VALUE))
        );

        panelcards.add(newtask, "newtask");

        history.setBackground(new java.awt.Color(102, 102, 255));
        history.setLayout(null);

        jPanel6.setBackground(new java.awt.Color(255, 0, 0));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("HISTORY");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
        );

        history.add(jPanel6);
        jPanel6.setBounds(0, 0, 878, 47);

        taskidcombo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        taskidcombo.setToolTipText("");
        taskidcombo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        taskidcombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                taskidcomboItemStateChanged(evt);
            }
        });
        taskidcombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taskidcomboActionPerformed(evt);
            }
        });
        history.add(taskidcombo);
        taskidcombo.setBounds(110, 100, 240, 30);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("SEARCH:");
        history.add(jLabel13);
        jLabel13.setBounds(30, 100, 80, 30);

        jButton9.setBackground(new java.awt.Color(255, 0, 0));
        jButton9.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons8_refresh_32.png"))); // NOI18N
        jButton9.setText("REFRESH");
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        history.add(jButton9);
        jButton9.setBounds(260, 490, 150, 40);

        jButton10.setBackground(new java.awt.Color(255, 0, 0));
        jButton10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_32.png"))); // NOI18N
        jButton10.setText("DELETE RECORD");
        jButton10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        history.add(jButton10);
        jButton10.setBounds(420, 490, 210, 40);

        jButton11.setBackground(new java.awt.Color(255, 0, 0));
        jButton11.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete_history_32.png"))); // NOI18N
        jButton11.setText("CLEAR HISTORY");
        jButton11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        history.add(jButton11);
        jButton11.setBounds(640, 490, 210, 40);

        ctable.setAutoCreateRowSorter(true);
        ctable.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ctable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ctable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        ctable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        ctable.setEnabled(false);
        ctable.setRowHeight(20);
        ctable.setSurrendersFocusOnKeystroke(true);
        jScrollPane3.setViewportView(ctable);

        history.add(jScrollPane3);
        jScrollPane3.setBounds(30, 140, 820, 330);

        panelcards.add(history, "history");

        jSplitPane1.setRightComponent(panelcards);

        getContentPane().add(jSplitPane1);
        jSplitPane1.setBounds(-10, 46, 1000, 760);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel2MouseDragged(evt);
            }
        });
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel2MousePressed(evt);
            }
        });
        jPanel2.setLayout(null);

        jButton5.setBackground(new java.awt.Color(0, 0, 0));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons8_close_window_32px.png"))); // NOI18N
        jButton5.setContentAreaFilled(false);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton5);
        jButton5.setBounds(940, 0, 40, 40);

        jButton6.setBackground(new java.awt.Color(0, 0, 0));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons8_compress_32px.png"))); // NOI18N
        jButton6.setContentAreaFilled(false);
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton6);
        jButton6.setBounds(890, 0, 40, 40);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Tasks. ©Dcoder Solutions");
        jPanel2.add(jLabel7);
        jLabel7.setBounds(10, 0, 210, 40);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 990, 40);

        setSize(new java.awt.Dimension(990, 785));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void jPanel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_jPanel2MousePressed

    private void jPanel2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseDragged
        int xx = evt.getXOnScreen();
        int yy = evt.getYOnScreen();
        this.setLocation(xx-x,yy-y);
    }//GEN-LAST:event_jPanel2MouseDragged

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       this.setState(ICONIFIED);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        MainTask m1 = new MainTask();
        int result = JOptionPane.showConfirmDialog(m1,
            "Do you want to Exit ?", "Confirmation ",
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION)
          System.exit(0);
        else if (result == JOptionPane.NO_OPTION)
          new MainTask().setDefaultCloseOperation(m1.DO_NOTHING_ON_CLOSE);
      
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       cardLayout.show(panelcards,"home");
       loadctask();
       loadntask();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        loadctask();
        loadntask();
        duedate.cleanup();
        subject.setText(null);
        addtask.setText(null);
        tempid.setText(null);
        cardLayout.show(panelcards,"newtask");
          try{
         Statement s = dbcon.mycon().createStatement();
         ResultSet rs = s.executeQuery("Select max(taskid) from current");
         rs.next();
         rs.getString("max(taskid)");
                 if(rs.getString("max(taskid)")==null || (tempid.getText() == null ? rs.getString("max(taskid)") == null : tempid.getText().equals(rs.getString("max(taskid)"))))
                 {      
                     tempid.setText("TASKID");
                     tempid.setText(ldtString);
                 }
                 else 
                 {  
                    tempid.setText("TASKID");
                    tempid.setText(ldtString);
                 }
    }catch(InstantiationException | IllegalAccessException | SQLException e)
    {
        JOptionPane.showMessageDialog(this,e,"Error",JOptionPane.ERROR_MESSAGE);
    }
          jButton1.setForeground(new java.awt.Color(255, 255, 255));
          jButton2.setForeground(new java.awt.Color(255, 255, 0));
          jButton3.setForeground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
      emptyhistory();
      taskidcombo.removeAllItems();
      loadtaskid();
      loadhtable(); 
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try{
            String sql = "Insert into completed values (?,?,?,?,?)";
            PreparedStatement ps = dbcon.mycon().prepareStatement(sql);

            ps.setString(1,ctid.getText());
            ps.setString(2,sub.getText());
            ps.setString(3,ctp.getText());
            ps.setString(4,sdate);
            ps.setString(5,dd.getText());
            ps.executeUpdate();
        }catch(Exception e){}
        try {
            String sql="Delete from current where taskid='"+t+"'";
            PreparedStatement ps = dbcon.mycon().prepareStatement(sql);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Task Completed..!");
        } catch (SQLException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MainTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadctask();
        loadntask();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void subjectKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_subjectKeyPressed
        String sub = subject.getText();
        int length = sub.length();
        if(length > 50 )
        {
            subject.setEditable(false);
            JOptionPane.showMessageDialog(this,"Subject should be of 50 characters or less...!","Alert",JOptionPane.WARNING_MESSAGE);
        }
        if(evt.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE ||evt.getExtendedKeyCode()==KeyEvent.VK_DELETE)
        {
            subject.setEditable(true);
        }

    }//GEN-LAST:event_subjectKeyPressed

    private void subjectKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_subjectKeyTyped

    }//GEN-LAST:event_subjectKeyTyped

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String s = ((JTextField)duedate.getDateEditor().getUiComponent()).getText();
        if(!"".equals(subject.getText()) && !"".equals(addtask.getText())&&!"".equals(s))
        {
            try{
                String sql = "insert into current values(?,?,?,?,?)";
                PreparedStatement ps = dbcon.mycon().prepareStatement(sql);
                String no = tempid.getText();
                ps.setString(1,tempid.getText());
                ps.setString(2, subject.getText());
                ps.setString(3, addtask.getText());
                ps.setString(4,date.format(formatter));
                ps.setString(5,((JTextField)duedate.getDateEditor().getUiComponent()).getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Task Created...!");

            } catch (SQLException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MainTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadctask();
            cardLayout.show(panelcards,"home");
            this.dispose();
            new MainTask().setVisible(true);
            duedate.setCalendar(null);
            subject.setText(null);
            addtask.setText(null);
            tempid.setText(null);
            jButton1.setForeground(new java.awt.Color(255, 255, 0));
            jButton2.setForeground(new java.awt.Color(255, 255, 255));
            jButton3.setForeground(new java.awt.Color(255, 255, 255));
        }
        else
        {
            JOptionPane.showMessageDialog(this,"Please enter all details..!","Incomplete details",JOptionPane.ERROR_MESSAGE);
        }
        

    }//GEN-LAST:event_jButton8ActionPerformed

    private void taskidcomboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_taskidcomboItemStateChanged
        searchtask();
    }//GEN-LAST:event_taskidcomboItemStateChanged

    private void taskidcomboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taskidcomboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_taskidcomboActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        taskidcombo.removeAllItems();
        loadtaskid();
        loadhtable();
        emptyhistory();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        try{

            String cell = (String)taskidcombo.getSelectedItem();
            if(cell == null || cell == "SELECT TASK ID")
            {
                JOptionPane.showMessageDialog(this,"Please select TaskID.!","Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                String sql = "DELETE from completed where taskid like '%"+cell+"%'";
                PreparedStatement p = dbcon.mycon().prepareStatement(sql);
                p.executeUpdate();
                DefaultTableModel model = (DefaultTableModel)ctable.getModel();
                model.setRowCount(0);
                JOptionPane.showMessageDialog(this,"Record Deleted..!","",JOptionPane.OK_OPTION);
            }
            taskidcombo.removeAllItems();
            loadtaskid();
        }catch(SQLException | HeadlessException e){JOptionPane.showMessageDialog(this,e,"Error",JOptionPane.ERROR_MESSAGE);} catch (InstantiationException ex) {
            Logger.getLogger(MainTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        emptyhistory();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        int result = JOptionPane.showConfirmDialog(this, "Do you want to clear all history ?", "Confirmation ", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION)
        try{
            String sql = "DELETE from completed";
            PreparedStatement p = dbcon.mycon().prepareStatement(sql);
            p.executeUpdate();
            JOptionPane.showMessageDialog(this,"All history cleared...!","Success",JOptionPane.WARNING_MESSAGE);
            cardLayout.show(panelcards,"newtask");
            loadctask();
            loadntask();
            
        }catch(SQLException | HeadlessException e){JOptionPane.showMessageDialog(this,e,"Error",JOptionPane.ERROR_MESSAGE);} catch (InstantiationException ex) {
            Logger.getLogger(MainTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainTask.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton11ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainTask().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea addtask;
    private javax.swing.JTable ctable;
    private javax.swing.JLabel ctid;
    private javax.swing.JTextPane ctp;
    private javax.swing.JLabel dd;
    private javax.swing.JLabel dd2;
    public static com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JPanel history;
    private javax.swing.JPanel home;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel newtask;
    private javax.swing.JPanel panelcards;
    private javax.swing.JLabel sub;
    private javax.swing.JTextField subject;
    private javax.swing.JComboBox taskidcombo;
    private javax.swing.JLabel tempid;
    private javax.swing.JLabel utl;
    private javax.swing.JLabel utl1;
    private javax.swing.JLabel utl2;
    private javax.swing.JLabel utp;
    // End of variables declaration//GEN-END:variables
}
