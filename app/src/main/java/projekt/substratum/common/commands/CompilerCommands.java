/*
 * Copyright (c) 2016-2017 Projekt Substratum
 * This file is part of Substratum.
 *
 * Substratum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Substratum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Substratum.  If not, see <http://www.gnu.org/licenses/>.
 */

package projekt.substratum.common.commands;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import projekt.substratum.BuildConfig;
import projekt.substratum.common.References;
import projekt.substratum.common.Systems;

import static projekt.substratum.common.References.ENABLE_AAPT_OUTPUT;
import static projekt.substratum.common.References.permissionSamsungOverlay;
import static projekt.substratum.common.Systems.getDeviceID;

public enum CompilerCommands {
    ;

    /**
     * Create the overlay's manifest
     *
     * @param context             Context
     * @param overlayPackage      Overlay's package name
     * @param themeName           Theme name
     * @param variantName         Variant name
     * @param baseVariantName     Base variant name
     * @param versionName         Version
     * @param targetPackage       Target package
     * @param themeParent         Theme Parent
     * @param themeOms            OMS Support
     * @param legacyPriority      Legacy Priority
     * @param baseVariantNull     Base variant available?
     * @param type1a              Type 1a
     * @param type1b              Type 1b
     * @param type1c              Type 1c
     * @param type2               Type 2
     * @param type3               Type 3
     * @param type4               Type 4
     * @param packageNameOverride Override package name
     * @return Returns a string that contains the full manifest file
     */
    public static String createOverlayManifest(Context context,
                                               String overlayPackage,
                                               String themeName,
                                               String variantName,
                                               String baseVariantName,
                                               String versionName,
                                               String targetPackage,
                                               String themeParent,
                                               Boolean themeOms,
                                               Integer legacyPriority,
                                               Boolean baseVariantNull,
                                               String type1a,
                                               String type1b,
                                               String type1c,
                                               String type2,
                                               String type3,
                                               String type4,
                                               String packageNameOverride) {
        String packageName;
        if (baseVariantNull) {
            packageName = overlayPackage + '.' + themeName;
        } else {
            packageName = overlayPackage + '.' + themeName +
                    variantName + baseVariantName;
        }
        if (isNotNullOrEmpty(packageNameOverride)) {
            packageName = packageNameOverride;
        }

        boolean showOverlayInSamsungSettings =
                Systems.isSamsungDevice(context) && References.toggleShowSamsungOverlayInSettings;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // root elements
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("manifest");
            rootElement.setAttribute("xmlns:android",
                    "http://schemas.android.com/apk/res/android");
            rootElement.setAttribute("package", packageName);
            rootElement.setAttribute("android:versionName", versionName);

            Element overlayElement = document.createElement("overlay");
            if (!themeOms)
                overlayElement.setAttribute("android:priority", String.valueOf(legacyPriority));
            overlayElement.setAttribute("android:targetPackage", targetPackage);
            if (showOverlayInSamsungSettings) {
                Element samsungPermissionElement = document.createElement("uses-permission");
                samsungPermissionElement.setAttribute("android:name", permissionSamsungOverlay);
                overlayElement.appendChild(samsungPermissionElement);
            }
            rootElement.appendChild(overlayElement);

            Element applicationElement = document.createElement("application");
            applicationElement.setAttribute("android:label", packageName);
            applicationElement.setAttribute("allowBackup", "false");
            applicationElement.setAttribute("android:hasCode", "false");

            Element metadataOverlayDevice = document.createElement("meta-data");
            metadataOverlayDevice.setAttribute("android:name", References.metadataOverlayDevice);
            metadataOverlayDevice.setAttribute("android:value", getDeviceID(context));
            applicationElement.appendChild(metadataOverlayDevice);

            Element metadataOverlayParent = document.createElement("meta-data");
            metadataOverlayParent.setAttribute("android:name", References.metadataOverlayParent);
            metadataOverlayParent.setAttribute("android:value", themeParent);
            applicationElement.appendChild(metadataOverlayParent);

            Element metadataOverlayTarget = document.createElement("meta-data");
            metadataOverlayTarget.setAttribute("android:name", References.metadataOverlayTarget);
            metadataOverlayTarget.setAttribute("android:value", targetPackage);
            applicationElement.appendChild(metadataOverlayTarget);

            Element metadataOverlayType1a = document.createElement("meta-data");
            metadataOverlayType1a.setAttribute("android:name", References.metadataOverlayType1a);
            metadataOverlayType1a.setAttribute("android:value", type1a);
            applicationElement.appendChild(metadataOverlayType1a);

            Element metadataOverlayType1b = document.createElement("meta-data");
            metadataOverlayType1b.setAttribute("android:name", References.metadataOverlayType1b);
            metadataOverlayType1b.setAttribute("android:value", type1b);
            applicationElement.appendChild(metadataOverlayType1b);

            Element metadataOverlayType1c = document.createElement("meta-data");
            metadataOverlayType1c.setAttribute("android:name", References.metadataOverlayType1c);
            metadataOverlayType1c.setAttribute("android:value", type1c);
            applicationElement.appendChild(metadataOverlayType1c);

            Element metadataOverlayType2 = document.createElement("meta-data");
            metadataOverlayType2.setAttribute("android:name", References.metadataOverlayType2);
            metadataOverlayType2.setAttribute("android:value", type2);
            applicationElement.appendChild(metadataOverlayType2);

            Element metadataOverlayType3 = document.createElement("meta-data");
            metadataOverlayType3.setAttribute("android:name", References.metadataOverlayType3);
            metadataOverlayType3.setAttribute("android:value", type3);
            applicationElement.appendChild(metadataOverlayType3);

            Element metadataOverlayType4 = document.createElement("meta-data");
            metadataOverlayType4.setAttribute("android:name", References.metadataOverlayType4);
            metadataOverlayType4.setAttribute("android:value", type4);
            applicationElement.appendChild(metadataOverlayType4);

            Element metadataOverlayVersion = document.createElement("meta-data");
            metadataOverlayVersion.setAttribute("android:name", References.metadataOverlayVersion);
            metadataOverlayVersion.setAttribute("android:value", String.valueOf(BuildConfig
                    .VERSION_CODE));
            applicationElement.appendChild(metadataOverlayVersion);

            rootElement.appendChild(applicationElement);
            document.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source domSource = new DOMSource(document);
            StringWriter outWriter = new StringWriter();
            Result streamResult = new StreamResult(outWriter);
            transformer.transform(domSource, streamResult);

            return outWriter.getBuffer().toString();
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Helper function to easily check whether a String object is null or empty
     *
     * @param string String object
     * @return True, then it is Null or Empty
     */
    private static boolean isNotNullOrEmpty(CharSequence string) {
        return (string != null) && (string.length() != 0);
    }

    /**
     * Create the AAPT working shell commands
     *
     * @param work_area          Working area
     * @param targetPkg          Target package to build against
     * @param overlay_package    Overlay package
     * @param theme_name         Theme name
     * @param legacySwitch       Fallback support
     * @param additional_variant Additional variant (type2)
     * @param asset_replacement  Asset replacement (type4)
     * @param context            Context
     * @param dir                Volatile directory to keep changes in
     * @return Returns a string to allow the app to execute
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    public static String createAAPTShellCommands(String work_area,
                                                 String targetPkg,
                                                 String overlay_package,
                                                 String theme_name,
                                                 boolean legacySwitch,
                                                 CharSequence additional_variant,
                                                 CharSequence asset_replacement,
                                                 Context context,
                                                 String dir) {
        StringBuilder sb = new StringBuilder();
        // Initialize the AAPT command
        sb.append(context.getFilesDir().getAbsolutePath() + "/aapt p ");
        // Compile with specified manifest
        sb.append("-M " + work_area + "/AndroidManifest.xml ");
        // If the user picked a variant (type2), compile multiple directories
        sb.append(((isNotNullOrEmpty(additional_variant)) ?
                ("-S " + work_area + '/' + "type2_" + additional_variant + "/ ") : ""));
        // If the user picked an asset variant (type4), compile multiple directories
        sb.append(((isNotNullOrEmpty(asset_replacement)) ?
                ("-A " + work_area + "/assets/ ") : ""));
        // We will compile a volatile directory where we make temporary changes to
        sb.append("-S " + work_area + dir + "/ ");
        // Build upon the system's Android framework
        sb.append("-I " + "/system/framework/framework-res.apk ");
        // If running on the AppCompat commits (first run), it will build upon the app too
        if (targetPkg != null && !targetPkg.equals("null")) {
            sb.append((legacySwitch) ? "" : ("-I " + targetPkg + ' '));
        }

        // Specify the file output directory
        sb.append("-F " + work_area + '/' + overlay_package + '.' +
                theme_name + "-unsigned.apk ");
        // arguments to conclude the AAPT build
        if (ENABLE_AAPT_OUTPUT) {
            sb.append("-v ");
        }
        // Allow themers to append new resources
        sb.append("--include-meta-data ");
        sb.append("--auto-add-overlay ");
        // Overwrite all the files in the internal storage
        sb.append("-f ");
        sb.append('\n');

        return sb.toString();
    }

    /**
     * Create the ZipAlign shell commands
     *
     * @param context     Context
     * @param source      Source
     * @param destination Destination
     * @return Returns a string that is executable by the application
     */
    public static String createZipAlignShellCommands(Context context,
                                                     String source,
                                                     String destination) {
        // Initialize the ZipAlign command
        String ret = context.getFilesDir().getAbsolutePath() + "/zipalign 4 ";
        // Supply the source
        ret += source + ' ';
        // Supply the destination
        ret += destination;

        return ret;
    }
}