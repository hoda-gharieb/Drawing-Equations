package drawEqu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This class controls the interface of the project it also has objects from the
 * rest of the classes created in this project
 * 
 * @author Hoda, Menna
 * 
 */
public class projectRun extends JFrame {
    
	// main panel in the main frame
	private JPanel contentPane;
	
	// tabbedPane Panels
	private final JPanel panel_1 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	
	// text fields used in the tabbedPane while browsing for files
	private final JTextField tf1 = new JTextField();
	private final JTextField tf2 = new JTextField();
	private final JTextField tf3 = new JTextField();
	
	// Buttons used in tabbedPane for browsing generating equations and images
	private final JButton btn1 = new JButton("Browse");	
	private final JButton btn2 = new JButton("Browse");
	private final JButton btnFormEquation = new JButton("Form equation");
	private final JButton btnLoadImage = new JButton("Load Image");
	
	// JFileChooser used to choose your an image from your PC to form an equation for it in a file which you chooses too 
	//and to load image from a file you chooses too.	
	private JFileChooser fc = new JFileChooser();
	
	// Carry the extension of the images and files you choose in the project
	private String txtFile;
	private String txtFile2;
	private String photo;
	
	// let you choose the color of the image
	private JComboBox comboBox = new JComboBox();
	
	// carry the lines detected in the image you chooses to form an equation for it.
	private Vector<houghLine> Hlines;
	
	// carry the image you chooses 
	private BufferedImage image;
	
	// carry the parameters of the equations calculated for the image.
	private Vector<Double> Equ = new Vector();
	
	// carry the color type you chooses from the JComboBox for your drawing
	private Color imageColor;
	
	// an object from class hough transform which analysis your image and detect main lines
	private houghTransform houghT;
	
	// an object of class drawing which generate a picture from an equation which is already made and saved
	private drawing Picture;

	/**
	 * The main function from which the project starts running
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					projectRun frame = new projectRun();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * constructor calls init function
	 * it initiates the value of the Strings containing files and image names
	 */
	public projectRun() {
		photo = txtFile = txtFile2 = "--";
		imageColor = Color.BLACK;
		init();
	}

	/**
	 * form an equation for every line in an image and saves it on a file
	 */
	public void formEquations() {
		Equ.clear();
		for (int j = 0; j < Hlines.size(); j++) {
			houghLine line = Hlines.elementAt(j);
			Vector<Double> D = line.formEquation(image);
			Equ.addAll(D);
		}
		PrintWriter out;
		try {
			out = new PrintWriter(txtFile);
			for (int i = 0; i < Equ.size(); i++)
				out.println(Equ.get(i));
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,
					"An error has occured, try again.");
		}
		JOptionPane.showMessageDialog(null, "Equations saved.");
	}

	/**
	 * convert an equation to an image by changing every equation saved on a
	 * file for a line to a line and drawing it
	 */
	public void formImage() {
		try {
			Scanner input = new Scanner(new File(txtFile2));
			double y = input.nextDouble();
			Picture = new drawing(txtFile2, imageColor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,
					"An error has occured, try again.");
		}

	}

	/**
	 * Organizing and forming the user interface along with controlling the
	 * formulation of equations for images and converting the equations back to
	 * images
	 */
	public void init() {

		// ====== set contentPane =======//
		setBackground(Color.PINK);
		setForeground(Color.CYAN);
		setTitle("Images Equations");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 679, 405);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 240, 245));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ====== set TabbedPane =======//
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(21, 25, 600, 330);
		contentPane.add(tabbedPane);
		tf2.setBounds(10, 124, 323, 20);
		tf2.setColumns(10);
		tf1.setBounds(10, 54, 323, 21);
		tf1.setColumns(10);
		panel_1.setBackground(new Color(216, 191, 216));

		tabbedPane.addTab("Form equation", null, panel_1, null);
		panel_1.setLayout(null);
		panel_1.add(tf1);

		// =========== browse for image button code =============//
		btn1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btn1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int Val_return = fc.showOpenDialog(null);
				if (Val_return == JFileChooser.APPROVE_OPTION) {
					photo = String.valueOf(fc.getSelectedFile());
					tf1.setText(photo);
				}
			}
		});
		btn1.setForeground(Color.MAGENTA);
		btn1.setBounds(439, 53, 101, 21);
		panel_1.add(btn1);

		panel_1.add(tf2);

		// =========== browse for equation file button code =============//
		btn2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btn2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int Val_return = fc.showOpenDialog(null);
				if (Val_return == JFileChooser.APPROVE_OPTION) {
					txtFile = String.valueOf(fc.getSelectedFile());
					tf2.setText(txtFile);
				}
			}
		});
		btn2.setForeground(Color.MAGENTA);
		btn2.setBounds(439, 123, 101, 21);
		panel_1.add(btn2);

		// ============ panel1 text fields labels ===========//
		JLabel lblBrowseForYour = new JLabel("Browse for your image");
		lblBrowseForYour.setForeground(new Color(139, 0, 139));
		lblBrowseForYour.setFont(new Font("Times New Roman", Font.BOLD
				| Font.ITALIC, 14));
		lblBrowseForYour.setBounds(22, 29, 153, 14);
		panel_1.add(lblBrowseForYour);

		JLabel lblBrowseForA = new JLabel(
				"Browse for a file to save the equation");
		lblBrowseForA.setForeground(new Color(139, 0, 139));
		lblBrowseForA.setFont(new Font("Times New Roman", Font.BOLD
				| Font.ITALIC, 14));
		lblBrowseForA.setBounds(22, 99, 232, 14);
		panel_1.add(lblBrowseForA);

		// ======= generation of function button code ===========//
		btnFormEquation.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (photo != "--" && txtFile != "--") {
					try {
						image = javax.imageio.ImageIO.read(new File(photo));
						houghT = new houghTransform(image.getWidth(), image
								.getHeight());
						houghT.getPoints(image);
						Hlines = houghT.getLines(20);

						formEquations();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null,
								"An error has occured, try again.");
					}

				}
			}
		});

		btnFormEquation.setForeground(new Color(255, 20, 147));
		btnFormEquation.setFont(new Font("Times New Roman", Font.BOLD
				| Font.ITALIC, 14));
		btnFormEquation.setBounds(240, 220, 136, 34);
		panel_1.add(btnFormEquation);

		panel_2.setBackground(new Color(216, 191, 216));

		tabbedPane.addTab("Load image", null, panel_2, null);
		panel_2.setLayout(null);

		tf3.setBounds(10, 76, 347, 20);
		panel_2.add(tf3);
		tf3.setColumns(10);

		// ========= browse for an equation file button code =============//
		JButton btn3 = new JButton("Browse");
		btn3.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btn3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int Val_return = fc.showOpenDialog(null);
				if (Val_return == JFileChooser.APPROVE_OPTION) {
					txtFile2 = String.valueOf(fc.getSelectedFile());
					tf3.setText(txtFile2);
				}
			}
		});
		btn3.setForeground(Color.MAGENTA);
		btn3.setBounds(419, 75, 117, 23);
		panel_2.add(btn3);

		// ========= panel_2 text field label code ===========//
		JLabel lblBrowseForThe = new JLabel("Browse for the equation file");
		lblBrowseForThe.setForeground(new Color(139, 0, 139));
		lblBrowseForThe.setFont(new Font("Times New Roman", Font.BOLD
				| Font.ITALIC, 14));
		lblBrowseForThe.setBounds(10, 51, 174, 14);
		panel_2.add(lblBrowseForThe);

		// ========= loading image button code ==========//
		btnLoadImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (txtFile2 != "--")
					formImage();
			}
		});
		btnLoadImage.setFont(new Font("Times New Roman", Font.BOLD
				| Font.ITALIC, 14));
		btnLoadImage.setForeground(new Color(255, 20, 147));
		btnLoadImage.setBounds(196, 231, 184, 33);

		panel_2.add(btnLoadImage);

		// ===== the comobobox for choosing the color code ====//
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox.getSelectedItem().equals("Black"))
					imageColor = Color.BLACK;
				else if (comboBox.getSelectedItem().equals("Pink"))
					imageColor = Color.PINK;
				else if (comboBox.getSelectedItem().equals("Red"))
					imageColor = Color.RED;
				else if (comboBox.getSelectedItem().equals("Green"))
					imageColor = Color.GREEN;
				else if (comboBox.getSelectedItem().equals("Cyan"))
					imageColor = Color.CYAN;
				else if (comboBox.getSelectedItem().equals("Blue"))
					imageColor = Color.BLUE;
				else if (comboBox.getSelectedItem().equals("Orange"))
					imageColor = Color.ORANGE;
			}
		});
		comboBox.setForeground(new Color(255, 0, 255));
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBox.setBounds(10, 147, 99, 20);
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "Black",
				"Pink", "Red", "Green", "Cyan", "Blue", "Orange" }));
		panel_2.add(comboBox);

		JLabel lblChooseAColor = new JLabel(
				"Choose a color for draing the image");
		lblChooseAColor.setFont(new Font("Times New Roman", Font.BOLD
				| Font.ITALIC, 14));
		lblChooseAColor.setForeground(new Color(139, 0, 139));
		lblChooseAColor.setBounds(10, 120, 233, 14);
		panel_2.add(lblChooseAColor);
	}
}
