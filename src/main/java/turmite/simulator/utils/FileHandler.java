package turmite.simulator.utils;

import turmite.simulator.TurmiteJFrame;
import turmite.simulator.models.Direction;
import turmite.simulator.models.Rule;
import turmite.simulator.ui.Dialogs;
import turmite.simulator.ui.RuleSelectorComboBox;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

/**
 * A class which handles Ruleset File import and Export. This class cannot be instantiated,
 * all its methods are static.
 */
public class FileHandler {
    private FileHandler() {}

    /**
     * Opens a {@code FileDialog} to select one file for importing. The initial directory and accepted file extension
     * is defined by the parameters.
     * <p>
     * The Ruleset file's name cannot be {@code RuleSelectorComboBox.NEW_RULESET_STR}.
     * <p>
     * The selected file is copied into {@code fileDir}, and the path of the file is returned.
     *
     * @param frame The parent frame of the File Dialog.
     * @param fileExt The extension of the Ruleset files.
     * @param fileDir The initial directory of the FileDialog.
     * @return The path of the file that has been copied into Ruleset directory, like so: {@code [fileDir]\[fileName][fileExt]}.
     * @throws IOException If an illegal file dialog mode is supplied.
     */
    public static String importRuleset(JFrame frame, String fileExt, String fileDir) throws IOException {
        FileDialog fd = new FileDialog(frame, "Import Rule", FileDialog.LOAD);
        fd.setDirectory("%UserProfile%\\Downloads");
        fd.setFile("*" + fileExt);
        fd.setVisible(true);

        String selectedFilePath = fd.getDirectory() + fd.getFile();
        if (selectedFilePath.equals("nullnull")) return null;
        if (fd.getFile().equals(RuleSelectorComboBox.NEW_RULESET_STR + fileExt)) {
            Dialogs.showErrorDialog(null, String.format("Ruleset name cannot be %s.", RuleSelectorComboBox.NEW_RULESET_STR));
            return null;
        }

        File selectedFile = new File(selectedFilePath);
        String copiedFilePath = Path.of(fileDir, selectedFile.getName()).toString();
        Files.copy(Path.of(selectedFilePath), Path.of(copiedFilePath), StandardCopyOption.REPLACE_EXISTING);

        return copiedFilePath;
    }

    /**
     * Opens a {@code FileDialog} to select a file path to export a Ruleset into.
     * The initial directory and the exported file's extension is defined by the parameters.
     * <p>
     * The Ruleset file's name cannot be {@code RuleSelectorComboBox.NEW_RULESET_STR}.
     * <p>
     * The Ruleset is exported as a JSON object, that has an array named {@code "ruleset"}, which contains Rule objects.
     *
     * @param frame The parent frame of the File Dialog.
     * @param ruleset The Ruleset to be exported.
     * @param fileExt The extension of the Ruleset file.
     * @param fileDir The initial directory of the FileDialog.
     * @throws FileNotFoundException If the file exists but is a directory rather than a regular file,
     * does not exist but cannot be created, or cannot be opened for any other reason.
     * @throws IllegalArgumentException If the selected filename is {@code RuleSelectorComboBox.NEW_RULESET_STR}, or an illegal file dialog mode is supplied.
     */
    public static void exportRuleset(JFrame frame, Ruleset ruleset, String fileDir, String fileExt) throws FileNotFoundException, IllegalArgumentException {
        FileDialog fd = new FileDialog(frame, "Export Ruleset", FileDialog.SAVE);
        fd.setDirectory(fileDir);
        fd.setFile("*" + fileExt);
        fd.setVisible(true);

        String rulesetFile = fd.getDirectory() + fd.getFile();
        if (rulesetFile.equals("nullnull")) return;
        if (fd.getFile().equals(RuleSelectorComboBox.NEW_RULESET_STR + fileExt)) throw new IllegalArgumentException(String.format("Ruleset name cannot be %s.",  RuleSelectorComboBox.NEW_RULESET_STR));

        OutputStream outputStream = new FileOutputStream(rulesetFile);
        JsonWriterFactory writerFactory = Json.createWriterFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true));
        JsonWriter writer = writerFactory.createWriter(outputStream);
        JsonObjectBuilder rulesetObject = Json.createObjectBuilder();
        JsonArrayBuilder rulesetArray = Json.createArrayBuilder();

        List<String> ruleNames = Rule.RuleCells.getRuleCellNames();
        for (Rule  rule : ruleset.getRules()) {
            JsonObject ruleObject = Json.createObjectBuilder()
                    .add(ruleNames.getFirst(), rule.getCurrState())
                    .add(ruleNames.get(1), rule.getCurrColor())
                    .add(ruleNames.get(2), String.valueOf(Direction.getCharFromTurnDir(rule.getTurnDir())))
                    .add(ruleNames.get(3), rule.getNewColor())
                    .add(ruleNames.getLast(), rule.getNewState())
                    .build();

            rulesetArray.add(ruleObject);
        }

        rulesetObject.add("ruleset", rulesetArray.build());
        writer.write(rulesetObject.build());
        writer.close();
    }

    public static void deleteFile(String fileName) throws IOException {
        Files.delete(Path.of(TurmiteJFrame.RULESET_DIR, fileName));
    }
}
