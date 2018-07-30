package com.nearix.mineroutput;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MinerOutputGUI extends Application {	
	
	FileChooser fsText = new FileChooser();
	FileChooser fsSave = new FileChooser();
	
	TextArea txArea = new TextArea();
	
	Button bOpen = new Button("Open File");
	Button bExport = new Button("Export to Excel");
	
	Label lExcel = new Label();
	
	Pane pane = new Pane();
		
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage stage) {
		
		stage.setTitle("Miner Output gets simple");
		stage.setWidth(1280);
		stage.setHeight(720);
		stage.setMinHeight(390);
		stage.setMinWidth(400);
		stage.setMaxHeight(1080);
		stage.setMaxWidth(1920);
		
		bOpen.setLayoutX(5);
		bOpen.setLayoutY(10);
		
		bExport.setLayoutX(stage.getWidth() - 130);
		bExport.setLayoutY(10);
		
		lExcel.setVisible(false);
		
		txArea.setLayoutX(5);
		txArea.setLayoutY(50);
		txArea.setPrefHeight(stage.getHeight() - 100);
		txArea.setPrefWidth(stage.getWidth() - 30);
		
		fsSave.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Doc (*.xls)", "*.xls"));
		
		pane.getChildren().addAll(bOpen, bExport, lExcel, txArea);
		stage.setScene(new Scene(pane, 300, 400));
		stage.show();
		
		bOpen.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				lExcel.setText("");
				File file = fsText.showOpenDialog(stage);
				readText(file);
			}			
		});
		
		bExport.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				writeFile(lExcel.getText(), fsSave.showSaveDialog(stage).toString());
			}			
		});
		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			txArea.setPrefSize(stage.getWidth() - 30, stage.getHeight() - 100);
		});
		stage.heightProperty().addListener((obs, oldVal, newVal) -> {
			txArea.setPrefSize(stage.getWidth() - 30, stage.getHeight() - 100);
		});
		
		stage.setOnCloseRequest(event -> {
			Platform.exit();
		});
	}
		
	public void readText(File file) {
		try {
			int i = 0;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				if(!(i < 16) && !(line.contains("hash order")) && !(line.contains(" x16r block"))) {
					line = line.substring(22, line.length());
					line = line.replaceAll("\\[01;37m ", "");
					line = line.replaceAll("\\[32m", "");
					line = line.replaceAll("\\[0m", "");
					line = line.replaceAll("\\[36m", "");
					line = line.replaceAll("\\[33m", "");
					if(line.contains("diff")) {
						line = line.replace(line.substring(line.indexOf("diff") - 1, line.indexOf(",") + 16), "");
						line = line.replace("yes!", "");
						line = line.replace("booooo", "");
					}
					txArea.appendText(line + "\n");
				}
				if(line.contains("accepted:")) {					
					if(line.contains("kH/s")) {
						line = line.replace("accepted: ", "");
						line = line.substring(line.indexOf(' ') + 1, line.indexOf('.'));
						lExcel.setText(lExcel.getText() + line + "\t");
					}
					if(line.contains("MH/s")) {
						line = line.replace("accepted: ", "");
						line = line.substring(line.indexOf(' ') + 1, line.indexOf('.') + 3);
						Double dBuffer = Double.parseDouble(line);
						dBuffer = dBuffer * 1000;		
						if(!(dBuffer.toString().length() > 7)) {
							lExcel.setText(lExcel.getText() + dBuffer.toString().replace(".0", "") + "\t");
						}
					}
				}
				i = i + 1;
			}
			reader.close();		
			System.out.println(lExcel.getText());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}	
	public static void writeFile(String text, String file) {

	    BufferedWriter f;
	    try {
	      f = new BufferedWriter(new FileWriter(file));
	      f.write(text);
	      f.close();
	    }
	    catch (IOException e) {
	      System.err.println(e.toString());
	    }		
	}	
}