import express from "express";
import path from "path";
import fs from "fs";
import { createServer as createViteServer } from "vite";

const app = express();
const PORT = 3000;

app.use(express.json());

// Utility to recursively list files in a directory
function getFilesRecursively(dir: string, baseDir: string = dir): { path: string; name: string; content: string }[] {
  let results: { path: string; name: string; content: string }[] = [];
  if (!fs.existsSync(dir)) return results;
  
  const list = fs.readdirSync(dir);
  list.forEach((file) => {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);
    
    // Skip build folders, node_modules, git, and other assets
    if (file === "node_modules" || file === "build" || file === ".git" || file === "dist" || file === ".gradle" || file === "assets") {
      return;
    }
    
    if (stat && stat.isDirectory()) {
      results = results.concat(getFilesRecursively(fullPath, baseDir));
    } else {
      // Exclude binary zip, exe, jar, etc. Only focus on readable files
      const ext = path.extname(file).toLowerCase();
      if ([".java", ".xml", ".kts", ".properties", ".json", ".example"].includes(ext)) {
        const relativePath = path.relative(baseDir, fullPath);
        const content = fs.readFileSync(fullPath, "utf-8");
        results.push({
          path: relativePath,
          name: file,
          content: content
        });
      }
    }
  });
  return results;
}

// API: Retrieve all Android files
app.get("/api/android-files", (req, res) => {
  try {
    const rootDir = process.cwd();
    const files = getFilesRecursively(rootDir);
    res.json({ success: true, files });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// API: Save an edited file
app.post("/api/save-file", (req, res) => {
  try {
    const { filePath, content } = req.body;
    if (!filePath || content === undefined) {
      return res.status(400).json({ success: false, error: "Missing filePath or content" });
    }

    // Security: Prevent Directory Traversal
    const safePath = path.resolve(process.cwd(), filePath);
    if (!safePath.startsWith(process.cwd())) {
      return res.status(403).json({ success: false, error: "Access denied: outside workspace bounds" });
    }

    // Create directories if needed
    fs.mkdirSync(path.dirname(safePath), { recursive: true });
    
    // Save file
    fs.writeFileSync(safePath, content, "utf-8");
    res.json({ success: true, message: `File saved successfully: ${filePath}` });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// API: Reconfigure the Android project (Name, Package, Theme colors, Seed data)
app.post("/api/reconfigure-project", (req, res) => {
  try {
    const { appName, packageName, primaryColor, accentColor, seedHabits } = req.body;
    const projectRoot = process.cwd();

    // 1. Update appName in strings.xml
    const stringsPath = path.join(projectRoot, "app", "src", "main", "res", "values", "strings.xml");
    if (fs.existsSync(stringsPath)) {
      let stringsContent = fs.readFileSync(stringsPath, "utf-8");
      
      // Update app_name node
      stringsContent = stringsContent.replace(
        /<string name="app_name">.*?<\/string>/,
        `<string name="app_name">${appName}</string>`
      );
      fs.writeFileSync(stringsPath, stringsContent, "utf-8");
    }

    // 2. Update colors in colors.xml
    const colorsPath = path.join(projectRoot, "app", "src", "main", "res", "values", "colors.xml");
    if (fs.existsSync(colorsPath)) {
      let colorsContent = fs.readFileSync(colorsPath, "utf-8");
      
      if (primaryColor) {
        colorsContent = colorsContent.replace(
          /<color name="primary">#.*?<\/color>/,
          `<color name="primary">${primaryColor}</color>`
        );
      }
      if (accentColor) {
        colorsContent = colorsContent.replace(
          /<color name="accent">#.*?<\/color>/,
          `<color name="accent">${accentColor}</color>`
        );
      }
      fs.writeFileSync(colorsPath, colorsContent, "utf-8");
    }

    // 3. Update build.gradle.kts settings
    const settingsPath = path.join(projectRoot, "settings.gradle.kts");
    if (fs.existsSync(settingsPath)) {
      let settingsContent = fs.readFileSync(settingsPath, "utf-8");
      settingsContent = settingsContent.replace(
        /rootProject\.name\s*=\s*".*?"/,
        `rootProject.name = "${appName}"`
      );
      fs.writeFileSync(settingsPath, settingsContent, "utf-8");
    }

    // 4. Update namespace and applicationId in app/build.gradle.kts
    const appGradlePath = path.join(projectRoot, "app", "build.gradle.kts");
    let currentPackage = "com.example.lifeflowpremium";
    if (fs.existsSync(appGradlePath)) {
      let appGradleContent = fs.readFileSync(appGradlePath, "utf-8");
      
      // Try to find old namespace
      const namespaceMatch = appGradleContent.match(/namespace\s*=\s*"(.*?)"/);
      if (namespaceMatch && namespaceMatch[1]) {
        currentPackage = namespaceMatch[1];
      }

      if (packageName) {
        appGradleContent = appGradleContent.replace(
          /namespace\s*=\s*".*?"/,
          `namespace = "${packageName}"`
        );
        appGradleContent = appGradleContent.replace(
          /applicationId\s*=\s*".*?"/,
          `applicationId = "${packageName}"`
        );
        fs.writeFileSync(appGradlePath, appGradleContent, "utf-8");
      }
    }

    // 5. Package Name Reorganization (folders movement)
    // Moving Java files if packageName has changed!
    if (packageName && packageName !== currentPackage) {
      const oldPackageDir = path.join(projectRoot, "app", "src", "main", "java", ...currentPackage.split("."));
      const newPackageDir = path.join(projectRoot, "app", "src", "main", "java", ...packageName.split("."));
      
      if (fs.existsSync(oldPackageDir) && oldPackageDir !== newPackageDir) {
        // Read java files
        const files = fs.readdirSync(oldPackageDir);
        fs.mkdirSync(newPackageDir, { recursive: true });
        
        files.forEach((file) => {
          if (file.endsWith(".java")) {
            const oldFilePath = path.join(oldPackageDir, file);
            const newFilePath = path.join(newPackageDir, file);
            
            let source = fs.readFileSync(oldFilePath, "utf-8");
            // Update package declaration in Java code
            source = source.replace(
              /package\s+[\w\.]+;/,
              `package ${packageName};`
            );
            // Replace any imports of R of old package
            source = source.replace(
              new RegExp(`import\\s+${currentPackage}\\.R;`),
              `import ${packageName}.R;`
            );
            
            fs.writeFileSync(newFilePath, source, "utf-8");
            fs.unlinkSync(oldFilePath);
          }
        });

        // Clean up old directory structure if empty
        let currentCleanPath = oldPackageDir;
        const rootJavaDir = path.join(projectRoot, "app", "src", "main", "java");
        while (currentCleanPath !== rootJavaDir) {
          if (fs.existsSync(currentCleanPath) && fs.readdirSync(currentCleanPath).length === 0) {
            fs.rmdirSync(currentCleanPath);
          }
          currentCleanPath = path.dirname(currentCleanPath);
        }
      }

      // Update AndroidManifest.xml activity path if needed
      const manifestPath = path.join(projectRoot, "app", "src", "main", "AndroidManifest.xml");
      if (fs.existsSync(manifestPath)) {
        let manifestContent = fs.readFileSync(manifestPath, "utf-8");
        manifestContent = manifestContent.replace(
          new RegExp(`android:name="${currentPackage}\\.MainActivity"`),
          `android:name="${packageName}.MainActivity"`
        );
        // Fallback for simple classnames
        manifestContent = manifestContent.replace(
          /android:name="\.(MainActivity)"/,
          `android:name="${packageName}.MainActivity"`
        );
        fs.writeFileSync(manifestPath, manifestContent, "utf-8");
      }
    }

    // 6. Update seed habits in MainActivity.java
    const targetPackage = packageName || currentPackage;
    const mainActivityPath = path.join(projectRoot, "app", "src", "main", "java", ...targetPackage.split("."), "MainActivity.java");
    if (fs.existsSync(mainActivityPath) && seedHabits && Array.isArray(seedHabits)) {
      let mainActivityContent = fs.readFileSync(mainActivityPath, "utf-8");
      
      // Build habits insertion block
      const insertionLines = seedHabits.map(habit => `            dbHelper.addHabit("${habit}");`).join("\n");
      const pattern = /if\s*\(habitList\.isEmpty\(\)\)\s*\{[\s\S]*?\}/;
      const replacement = `if (habitList.isEmpty()) {
${insertionLines}
            habitList = dbHelper.getAllHabits();
        }`;
      
      mainActivityContent = mainActivityContent.replace(pattern, replacement);
      fs.writeFileSync(mainActivityPath, mainActivityContent, "utf-8");
    }

    res.json({ success: true, message: "Project reconfigured successfully!", currentPackage: targetPackage });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
});


// Dev & Production serving middlewares
async function startServer() {
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Development companion running at http://0.0.0.0:${PORT}`);
  });
}

startServer();
