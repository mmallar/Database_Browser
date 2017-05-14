
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	// global variables
	public String path = null;
	public TextArea text_area = new TextArea();
	public TextArea table_list = new TextArea();
	public List<String> tables = new ArrayList<>();
	public List<String> attributes = new ArrayList<>();
	private String search_condition;
	private String table_selected;
	private String attribute_selected;
	
	// to get all table & attribute names
	public void getDBInfo() throws ClassNotFoundException {
		List<String> to_tableList = new ArrayList<>();
		Class.forName("org.sqlite.JDBC");
		// SQL query handler
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
			DatabaseMetaData md = connection.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				// System.out.println(rs.getString(3));
				to_tableList.add(rs.getString(3));
				tables.add(rs.getString(3));
			}
			table_list.appendText("LIST OF TABLES IN DB: \n");
			for (String s : to_tableList) {
				table_list.appendText(s + "\n");
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	public void getAttributeInfo() throws ClassNotFoundException {
		String sql = "SELECT * FROM " + table_selected + ";";
		Class.forName("org.sqlite.JDBC");
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
			PreparedStatement pstmt;
			pstmt = connection.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			for(int j = 1; j < columnsNumber + 1; j++){
				attributes.add(rsmd.getColumnName(j));
				System.out.println(rsmd.getColumnName(j));
			}
		}catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public MenuButton createAttributeButton(){
		EventHandler<ActionEvent> att_selAction = att_selHandler();
		MenuButton btn_temp = new MenuButton();
		for(String element : attributes){
			MenuItem item = new MenuItem(element);
			item.setOnAction(att_selAction);
			btn_temp.getItems().add(item);			
		}
		btn_temp.setPrefSize(150, 30);
		btn_temp.setStyle("-fx-background-color: lightgreen");
		btn_temp.setText("Select Attribute");
		btn_temp.setFont(Font.font("Arail", FontWeight.BOLD, 15));
		btn_temp.setTextFill(Color.BLACK);
		btn_temp.setBorder(new Border(
				new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));
		return btn_temp;
	}
	
	// Custom query button click event handler
	public void ExecuteCustomQuery(String sql) throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		// SQL query handler
		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
			PreparedStatement pstmt;
			pstmt = connection.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			// container to hold input data to be displayed to the user
			List<String> to_TextArea = new ArrayList<>();
			for (int j = 1; j < columnsNumber + 1; j++) {
				if (j == columnsNumber) {
					System.out.print(rsmd.getColumnName(j) + "\n");
					to_TextArea.add(rsmd.getColumnName(j));
					to_TextArea.add("\n");
					to_TextArea.add("\n");
				} else {
					System.out.print(rsmd.getColumnName(j) + ", ");
					to_TextArea.add(rsmd.getColumnName(j));
				}
			}
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					String columnValue = rs.getString(i);
					System.out.print(columnValue + ", ");
					to_TextArea.add(columnValue);

				}
				System.out.println("");
				to_TextArea.add("\n");
			}
			for (String s : to_TextArea) {
				if (s == "\n") {
					text_area.appendText(" \n");
				} else {
					text_area.appendText(s + ", ");
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	// Start GUI implementation
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		ToolBar tb = new ToolBar();

		primaryStage.setTitle("Database Viewer UI");
		root.setStyle("-fx-background-color: beige");

		text_area.setPrefSize(2000, 2000);
		text_area.setEditable(false);
		ScrollPane sp_results = new ScrollPane(text_area);
		sp_results.setPrefSize(885, 850);
		// sp.setFitToWidth(true);

		table_list.setPrefSize(500, 2000);
		table_list.setEditable(false);
		ScrollPane sp_tableList = new ScrollPane(table_list);
		sp_tableList.setPrefSize(300, 850);
		sp_tableList.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp_tableList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		// Disable functionality without loading a database first
		if (path == null) {
			text_area.appendText("Please load a in a database before beginning");
		}

		tb.setPrefSize(50, 50);
		tb.setStyle("-fx-background-color: green");

		// Drop down quick query items for database project
		MenuItem menuItem1 = new MenuItem("List All Recent Customers");
		MenuItem menuItem2 = new MenuItem("List All Employees");
		MenuItem menuItem3 = new MenuItem("List All Inventory Items");

		MenuButton btn_Menu = new MenuButton("Options", null, menuItem1, menuItem2, menuItem3);
		btn_Menu.setPrefSize(150, 30);
		btn_Menu.setStyle("-fx-background-color: lightgreen");
		btn_Menu.setText("Quick Queries");
		btn_Menu.setFont(Font.font("Arail", FontWeight.BOLD, 15));
		btn_Menu.setTextFill(Color.BLACK);
		btn_Menu.setBorder(new Border(
				new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));

		// handles a recent customer query when clicked
		menuItem1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// clear the current text area of any text
				text_area.clear();
				try {
					// checks to see if there is a database that has been loaded
					// before continuing
					if (path == null) {
						text_area.clear();
						text_area.appendText("Error: Please load a in a database before beginning");
					} else {
						text_area.clear();
						ExecuteCustomQuery("Select * From Customer;");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		// handles menu item 2 query when clicked
		menuItem2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				text_area.clear();
				try {
					if (path == null) {
						text_area.clear();
						text_area.appendText("Error: Please load a in a database before beginning");
					} else {
						text_area.clear();
						ExecuteCustomQuery("Select * From Employee;");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		// handles menue item 3 when clicked
		menuItem3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				text_area.clear();
				try {
					if (path == null) {
						text_area.clear();
						text_area.appendText("Error: Please load a in a database before beginning");
					} else {
						text_area.clear();
						ExecuteCustomQuery("Select * From Item;");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		// implementation of a terminal to execute custom queries
		Button btn_Query = new Button();
		btn_Query.setPrefSize(150, 30);
		btn_Query.setStyle("-fx-background-color: lightgreen");
		btn_Query.setText("SQL Terminal");
		btn_Query.setFont(Font.font("Arail", FontWeight.BOLD, 15));
		btn_Query.setTextFill(Color.BLACK);
		btn_Query.setBorder(new Border(
				new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));

		btn_Query.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Clear the text area
				text_area.clear();
				// checks that a database is present before continuing
				if (path == null) {
					text_area.clear();
					text_area.appendText("Error: Please load a in a database before beginning");
				} else {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Custom SQL Query");
					dialog.setHeaderText("Please write your custom query here.");

					Optional<String> result = dialog.showAndWait();

					String entered = "";

					if (result.isPresent()) {
						entered = result.get();
						if (entered.toUpperCase().contains("SELECT") && entered.toUpperCase().contains("FROM")
								&& entered.contains(";")) {
							try {
								ExecuteCustomQuery(entered);
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							text_area.appendText("SQL Syntax Error. Try again.");
						}
					}
				}
			}
		});

		// implementation of a file browser to load a database
		Button btn_Browse = new Button();
		btn_Browse.setPrefSize(150, 30);
		btn_Browse.setStyle("-fx-background-color: lightgreen");
		btn_Browse.setText("Open Database");
		btn_Browse.setFont(Font.font("Arail", FontWeight.BOLD, 15));
		btn_Browse.setTextFill(Color.BLACK);
		btn_Browse.setBorder(new Border(
				new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));
		btn_Browse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// clearing the currently displayed text
				text_area.clear();
				table_list.clear();
				// open a new explorer window to search for a database
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				File file = fileChooser.showOpenDialog(primaryStage);
				// store the path of the database for SQL queries
				path = file.getAbsolutePath();
				try {
					getDBInfo();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// implementation of button used for searching the db
		Button btn_Search = new Button();
		btn_Search.setPrefSize(150, 30);
		btn_Search.setStyle("-fx-background-color: lightgreen");
		btn_Search.setText("Search");
		btn_Search.setFont(Font.font("Arail", FontWeight.BOLD, 15));
		btn_Search.setTextFill(Color.BLACK);
		btn_Search.setBorder(new Border(
				new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));
		btn_Search.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (path == null) {
					text_area.clear();
					text_area.appendText("Error: Please load a in a database before beginning");
				} else {
					Stage search = new Stage();
					search.initModality(Modality.APPLICATION_MODAL);
					search.initOwner(primaryStage);
					search.setTitle("DB Search Window");

					HBox searchHbox = new HBox(20);
					searchHbox.setStyle("-fx-background-color: beige");
					
					EventHandler<ActionEvent> table_selAction = table_selHandler();
					
					MenuButton btn_selTables = new MenuButton("Tables");
					for(String element : tables){
						MenuItem item = new MenuItem(element);
						item.setOnAction(table_selAction);
						btn_selTables.getItems().add(item);			
					}
					btn_selTables.setPrefSize(150, 30);
					btn_selTables.setStyle("-fx-background-color: lightgreen");
					btn_selTables.setText("Select Table");
					btn_selTables.setFont(Font.font("Arail", FontWeight.BOLD, 15));
					btn_selTables.setTextFill(Color.BLACK);
					btn_selTables.setBorder(new Border(
							new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));
					
					HBox row2HBox = new HBox();
					row2HBox.getChildren().addAll(btn_selTables);
					row2HBox.setAlignment(Pos.CENTER);
					
					
					MenuButton btn_selAttribute = createAttributeButton();
					HBox row4HBox = new HBox();
					row4HBox.getChildren().addAll(btn_selAttribute);
					row4HBox.setAlignment(Pos.CENTER);
					 
					Button btn_excSearch = new Button();
					btn_excSearch.setPrefSize(150, 30);
					btn_excSearch.setStyle("-fx-background-color: lightgreen");
					btn_excSearch.setText("Search");
					btn_excSearch.setFont(Font.font("Arail", FontWeight.BOLD, 15));
					btn_excSearch.setTextFill(Color.BLACK);
					btn_excSearch.setBorder(new Border(new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID,
							CornerRadii.EMPTY, BorderWidths.FULL)));
					btn_excSearch.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							text_area.clear();
							String query = "SELECT * FROM " + table_selected.toUpperCase() + ";";
							try {
								ExecuteCustomQuery(query);
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							search.close();
						}
					});

					HBox row3HBox = new HBox();
					row3HBox.getChildren().addAll(btn_excSearch);
					row3HBox.setAlignment(Pos.CENTER);

					
					searchHbox.getChildren().add(row2HBox);
					searchHbox.getChildren().add(row4HBox);
					searchHbox.getChildren().add(row3HBox);
					searchHbox.setAlignment(Pos.CENTER);
					Scene searchScene = new Scene(searchHbox, 800, 400);
					search.setScene(searchScene);
					search.show();
				}
			}
		});

		// adding the buttons to the gui toolbar display
		tb.getItems().add(btn_Menu);
		tb.getItems().add(btn_Query);
		tb.getItems().add(btn_Browse);
		tb.getItems().add(btn_Search);

		// placing the toolbar on top and scroll pane below it
		root.setTop(tb);
		root.setRight(sp_results);
		root.setLeft(sp_tableList);

		// set up the stage and display it to the user
		primaryStage.setScene(new Scene(root, 1200, 950));
		primaryStage.show();
	}
	
	private EventHandler<ActionEvent> table_selHandler() {
        return new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MenuItem mItem = (MenuItem) event.getSource();
                //System.out.println("Handler Worked");
                table_selected = mItem.getText();
                try {
					getAttributeInfo();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println(table_selected);
                
            }
        };
    }
	
	private EventHandler<ActionEvent> att_selHandler() {
        return new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
            	MenuItem mItem = (MenuItem) event.getSource();
            	attribute_selected = mItem.getText();
            	System.out.println(attribute_selected);
            }
        };
    }

	public static void main(String[] args) {
		launch(args);
	}
}
