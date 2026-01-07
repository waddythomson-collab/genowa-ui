package genowa.ui.screens;

import genowa.Database;
import genowa.generator.*;
import genowa.generator.genobj.AbstractGenerationObject;
import genowa.generator.genobj.CobolRatingDriver;
import genowa.generator.genobj.JavaRatingDriver;
import genowa.util.IString;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * Code generation workflow screen.
 * Allows users to configure and execute code generation.
 */
public class GenerationScreen extends BorderPane {
    
    // Configuration controls
    private ComboBox<String> insuranceLineCombo;
    private ComboBox<String> processTypeCombo;
    private ComboBox<String> generatorTypeCombo;
    private ComboBox<String> templateCombo;
    private TextField outputFileField;
    private TextArea logArea;
    
    // Generation state
    private AbstractGenerator currentGenerator;
    private boolean isGenerating = false;
    
    public GenerationScreen() {
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f5f5f5;");
        
        setTop(createHeader());
        setCenter(createMainContent());
        setBottom(createActionBar());
        
        // Initialize with default values
        initializeDefaults();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 15, 0));
        
        Label title = new Label("Code Generation");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        // Configuration row
        HBox configRow = new HBox(15);
        configRow.setAlignment(Pos.CENTER_LEFT);
        
        Label insLineLabel = new Label("Insurance Line:");
        insuranceLineCombo = new ComboBox<>();
        insuranceLineCombo.getItems().addAll("PA", "HO", "CA", "WC");
        insuranceLineCombo.setValue("PA");
        insuranceLineCombo.setPrefWidth(120);
        
        Label processTypeLabel = new Label("Process Type:");
        processTypeCombo = new ComboBox<>();
        processTypeCombo.getItems().addAll(
            "RATING", "EDITS", "UNDERWRITING", "KEYSET",
            "IO", "ISSUANCE", "RENEWAL"
        );
        processTypeCombo.setValue("RATING");
        processTypeCombo.setPrefWidth(120);
        processTypeCombo.setOnAction(e -> updateTemplateList());
        
        Label genTypeLabel = new Label("Language:");
        generatorTypeCombo = new ComboBox<>();
        generatorTypeCombo.getItems().addAll("COBOL", "Java");
        generatorTypeCombo.setValue("COBOL");
        generatorTypeCombo.setPrefWidth(120);
        generatorTypeCombo.setOnAction(e -> updateTemplateList());
        
        Label templateLabel = new Label("Template:");
        templateCombo = new ComboBox<>();
        templateCombo.setPrefWidth(250);
        templateCombo.setEditable(true);
        
        Label outputLabel = new Label("Output File:");
        outputFileField = new TextField();
        outputFileField.setPrefWidth(300);
        
        configRow.getChildren().addAll(
            insLineLabel, insuranceLineCombo,
            processTypeLabel, processTypeCombo,
            genTypeLabel, generatorTypeCombo,
            templateLabel, templateCombo,
            outputLabel, outputFileField
        );
        
        header.getChildren().addAll(title, configRow);
        return header;
    }
    
    private VBox createMainContent() {
        VBox content = new VBox(10);
        
        // Log area
        Label logLabel = new Label("Generation Log:");
        logLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        VBox.setVgrow(logArea, Priority.ALWAYS);
        
        content.getChildren().addAll(logLabel, logArea);
        return content;
    }
    
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_RIGHT);
        actionBar.setPadding(new Insets(15, 0, 0, 0));
        
        Button generateBtn = new Button("Generate Code");
        generateBtn.setStyle("-fx-background-color: #4a90d9; -fx-text-fill: white; -fx-font-weight: bold;");
        generateBtn.setPrefWidth(150);
        generateBtn.setOnAction(e -> generateCode());
        
        Button clearBtn = new Button("Clear Log");
        clearBtn.setOnAction(e -> logArea.clear());
        
        Button browseTemplateBtn = new Button("Browse Template...");
        browseTemplateBtn.setOnAction(e -> browseTemplate());
        
        Button browseOutputBtn = new Button("Browse Output...");
        browseOutputBtn.setOnAction(e -> browseOutput());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        actionBar.getChildren().addAll(
            browseTemplateBtn, browseOutputBtn, spacer, clearBtn, generateBtn
        );
        
        return actionBar;
    }
    
    private void initializeDefaults() {
        updateTemplateList();
        updateOutputFileName();
        
        // Listen for changes to update output file name
        insuranceLineCombo.setOnAction(e -> updateOutputFileName());
        processTypeCombo.setOnAction(e -> {
            updateTemplateList();
            updateOutputFileName();
        });
        generatorTypeCombo.setOnAction(e -> {
            updateTemplateList();
            updateOutputFileName();
        });
    }
    
    private void updateTemplateList() {
        String processType = processTypeCombo.getValue();
        String genType = generatorTypeCombo.getValue();
        templateCombo.getItems().clear();
        
        if ("COBOL".equals(genType)) {
            if ("RATING".equals(processType)) {
                templateCombo.getItems().addAll("rating.tpl", "rating_main.tpl");
            } else if ("EDITS".equals(processType)) {
                templateCombo.getItems().addAll("edits.tpl", "validation.tpl");
            } else if ("UNDERWRITING".equals(processType)) {
                templateCombo.getItems().addAll("underwriting.tpl");
            } else if ("IO".equals(processType)) {
                templateCombo.getItems().addAll("io_process.tpl", "io_main.tpl");
            } else if ("ISSUANCE".equals(processType)) {
                templateCombo.getItems().addAll("issuance_process.tpl", "issuance_main.tpl");
            } else if ("RENEWAL".equals(processType)) {
                templateCombo.getItems().addAll("renewal_process.tpl", "renewal_main.tpl");
            } else {
                templateCombo.getItems().addAll("generic.tpl");
            }
        } else if ("Java".equals(genType)) {
            if ("RATING".equals(processType)) {
                templateCombo.getItems().addAll("JavaRating.tpl");
            } else {
                templateCombo.getItems().addAll("JavaGeneric.tpl");
            }
        }
        
        if (!templateCombo.getItems().isEmpty()) {
            templateCombo.setValue(templateCombo.getItems().get(0));
        }
    }
    
    private void updateOutputFileName() {
        String insLine = insuranceLineCombo.getValue();
        String processType = processTypeCombo.getValue();
        String genType = generatorTypeCombo.getValue();
        
        if (insLine != null && processType != null && genType != null) {
            String extension = "COBOL".equals(genType) ? ".cbl" : ".java";
            String prefix = "COBOL".equals(genType) ? "" : "Java";
            String suffix = processType;
            outputFileField.setText(prefix + insLine + suffix + extension);
        }
    }
    
    private void browseTemplate() {
        // TODO: Implement file browser dialog
        log("Template browser not yet implemented");
    }
    
    private void browseOutput() {
        // TODO: Implement file browser dialog
        log("Output browser not yet implemented");
    }
    
    private void generateCode() {
        if (isGenerating) {
            log("Generation already in progress...");
            return;
        }
        
        String insLine = insuranceLineCombo.getValue();
        String processType = processTypeCombo.getValue();
        String genType = generatorTypeCombo.getValue();
        String template = templateCombo.getValue();
        String outputFile = outputFileField.getText();
        
        if (insLine == null || processType == null || genType == null || 
            template == null || outputFile == null || outputFile.isEmpty()) {
            showAlert("Missing Configuration", "Please fill in all required fields.");
            return;
        }
        
        isGenerating = true;
        log("========================================");
        log("Starting code generation...");
        log("Insurance Line: " + insLine);
        log("Process Type: " + processType);
        log("Language: " + genType);
        log("Template: " + template);
        log("Output File: " + outputFile);
        log("========================================");
        
        // Run generation in background thread
        CompletableFuture.runAsync(() -> {
            try {
                // Initialize database connection
                Database.getInstance().initialize();
                
                // Create data access
                JpaDataAccess dataAccess = new JpaDataAccess();
                if (!dataAccess.connect("")) {
                    Platform.runLater(() -> {
                        log("ERROR: Failed to connect to database");
                        isGenerating = false;
                    });
                    return;
                }
                
                Platform.runLater(() -> log("✓ Database connected"));
                
                // Import ProcessType
                genowa.core.ProcessType procType = genowa.core.ProcessType.valueOf(processType);
                
                // Create generator based on process type and language
                if ("IO".equals(processType) || "ISSUANCE".equals(processType) || "RENEWAL".equals(processType)) {
                    // Bulk processes use BulkProcessGenerator
                    if ("COBOL".equals(genType)) {
                        currentGenerator = new BulkProcessGenerator(dataAccess, procType);
                    } else {
                        // Java bulk processes - use Java generator with process type
                        currentGenerator = new JavaRatingGenerator(dataAccess);
                        currentGenerator.setProcType(procType);
                    }
                } else {
                    // Rating/Edits/Underwriting use rating generators
                    if ("COBOL".equals(genType)) {
                        currentGenerator = new CobolRatingGenerator(dataAccess);
                        currentGenerator.setProcType(procType);
                    } else {
                        currentGenerator = new JavaRatingGenerator(dataAccess);
                        currentGenerator.setProcType(procType);
                    }
                }
                
                Platform.runLater(() -> log("✓ Generator created: " + processType + " (" + genType + ")"));
                
                // Create generation object
                AbstractGenerationObject genObj;
                if ("COBOL".equals(genType)) {
                    genObj = new CobolRatingDriver(new IString(insLine));
                } else {
                    genObj = new JavaRatingDriver(new IString(insLine));
                }
                
                // Set output file name
                genObj.setGenFileName(new IString(outputFile));
                // Note: Template name is determined by getMainTemplateName() in the driver
                // For bulk processes, we may need to override the template selection
                
                // Set generation object on generator
                currentGenerator.setGenObj(genObj);
                
                Platform.runLater(() -> log("✓ Generation object configured"));
                
                // Execute generation
                Platform.runLater(() -> log("Executing generation..."));
                currentGenerator.genMainObject(insLine);
                
                Platform.runLater(() -> {
                    log("========================================");
                    log("✓ Generation completed successfully!");
                    log("Output written to: " + outputFile);
                    log("========================================");
                    isGenerating = false;
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    log("========================================");
                    log("ERROR: Generation failed");
                    log(e.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                    log("========================================");
                    isGenerating = false;
                });
            }
        });
    }
    
    private void log(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
            // Auto-scroll to bottom
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

