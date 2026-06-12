import React, { useState, useEffect } from 'react';
import { Smartphone, Code, Sliders, BookOpen, Layers, CheckCircle, Github, Sparkles, AlertCircle } from 'lucide-react';
import { FileItem, ProjectConfig } from './types';
import AndroidEmulator from './components/AndroidEmulator';
import CodeExplorer from './components/CodeExplorer';
import ProjectConfigurator from './components/ProjectConfigurator';
import SetupGuide from './components/SetupGuide';

export default function App() {
  const [activeTab, setActiveTab] = useState<'explorer' | 'configurator' | 'guide'>('explorer');
  const [files, setFiles] = useState<FileItem[]>([]);
  const [isLoadingFiles, setIsLoadingFiles] = useState(true);
  
  // App Config synchronized with backend colors.xml, strings.xml, app/build.gradle.kts
  const [config, setConfig] = useState<ProjectConfig>({
    appName: "LifeFlow Premium",
    packageName: "com.example.lifeflowpremium",
    primaryColor: "#0F172A",
    accentColor: "#10B981",
    seedHabits: [
      "Drink warm lemon water",
      "5-min mindful breathing stretch",
      "Journal 3 things I'm grateful for",
      "Read 10 pages of my book"
    ]
  });

  // Fetch all Android project files
  const fetchAndroidFiles = async () => {
    setIsLoadingFiles(true);
    try {
      const response = await fetch("/api/android-files");
      const data = await response.json();
      if (data.success) {
        setFiles(data.files);
        
        // Parse configurations dynamically from currently loaded files!
        // This is incredibly robust - keeps UI perfectly synced with the filesystem
        const stringsFile = data.files.find((f: FileItem) => f.name === "strings.xml");
        const colorsFile = data.files.find((f: FileItem) => f.name === "colors.xml");
        const gradleAppFile = data.files.find((f: FileItem) => f.name === "build.gradle.kts" && f.path.startsWith("app"));
        
        const newConfig = { ...config };
        
        if (stringsFile) {
          const appNameMatch = stringsFile.content.match(/<string name="app_name">(.*?)<\/string>/);
          if (appNameMatch && appNameMatch[1]) {
            newConfig.appName = appNameMatch[1];
          }
        }
        
        if (colorsFile) {
          const primaryMatch = colorsFile.content.match(/<color name="primary">(.*?)<\/color>/);
          const accentMatch = colorsFile.content.match(/<color name="accent">(.*?)<\/color>/);
          if (primaryMatch && primaryMatch[1]) newConfig.primaryColor = primaryMatch[1];
          if (accentMatch && accentMatch[1]) newConfig.accentColor = accentMatch[1];
        }

        if (gradleAppFile) {
          const namespaceMatch = gradleAppFile.content.match(/namespace\s*=\s*"(.*?)"/);
          if (namespaceMatch && namespaceMatch[1]) {
            newConfig.packageName = namespaceMatch[1];
          }
        }
        
        setConfig(newConfig);
      }
    } catch (error) {
      console.error("Error loading Android files:", error);
    } finally {
      setIsLoadingFiles(false);
    }
  };

  useEffect(() => {
    fetchAndroidFiles();
  }, []);

  // Save specific modified file to backend
  const handleSaveFile = async (filePath: string, content: string): Promise<boolean> => {
    try {
      const response = await fetch("/api/save-file", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ filePath, content })
      });
      const data = await response.json();
      if (data.success) {
        // Update local state without full reload
        setFiles(prev =>
          prev.map(f => f.path === filePath ? { ...f, content } : f)
        );
        return true;
      }
      return false;
    } catch (error) {
      console.error("Error saving file:", error);
      return false;
    }
  };

  // Reconfigure project files and rename source directories on backend
  const handleApplyServerChanges = async (newConfig: ProjectConfig) => {
    const response = await fetch("/api/reconfigure-project", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newConfig)
    });
    const data = await response.json();
    if (data.success) {
      // Reload updated file tree to show renamed folders immediately
      await fetchAndroidFiles();
    }
    return data;
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans text-slate-800" id="main-companion-layout">
      {/* Premium Header Bar */}
      <header className="bg-white border-b border-slate-100 py-4 px-6 sm:px-8 select-none shrink-0" id="top-branding-header">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-slate-900 flex items-center justify-center text-white shadow-md">
              <Smartphone className="w-5 h-5 text-emerald-400" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-base font-bold text-slate-900 tracking-tight">LifeFlow Premium</h1>
                <span className="text-[10px] bg-emerald-50 text-emerald-700 px-2 py-0.5 rounded-full font-bold uppercase tracking-wider flex items-center gap-1 border border-emerald-100">
                  <Sparkles className="w-3 h-3 text-emerald-500 animate-pulse" />
                  AndoidIDE Compatible
                </span>
              </div>
              <p className="text-xs text-slate-400">AndroidIDE Synchronized Companion & Configurator Workspace</p>
            </div>
          </div>
          
          <div className="flex items-center gap-2 text-slate-400 text-xs font-mono bg-slate-50 border border-slate-100 px-3 py-1.5 rounded-xl">
            <span>Branch: <span className="font-semibold text-slate-600">main</span></span>
            <span className="text-slate-200">|</span>
            <span>TargetIDE: <span className="font-semibold text-slate-600">AndroidIDE</span></span>
          </div>
        </div>
      </header>

      {/* Main Workspace Stage */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 py-8 grid grid-cols-1 lg:grid-cols-12 gap-8 items-start" id="companion-workspace-grid">
        
        {/* Left Side: Real-time Android Screen Simulation */}
        <div className="lg:col-span-5 xl:col-span-4 flex flex-col items-center shrink-0" id="left-emulator-column">
          <div className="text-center mb-4 select-none" id="emulator-column-header">
            <span className="text-xs uppercase tracking-widest font-bold text-slate-400">Layout Simulator</span>
          </div>
          <AndroidEmulator config={config} />
        </div>

        {/* Right Side: AndroidIDE Code Center */}
        <div className="lg:col-span-7 xl:col-span-8 space-y-6" id="right-dashboard-column">
          {/* Tabs header selector */}
          <div className="bg-white p-1 rounded-2xl border border-slate-100 shadow-2xs flex gap-1 select-none" id="tabs-bar">
            <button
              onClick={() => setActiveTab('explorer')}
              className={`flex-1 py-3 px-4 rounded-xl text-xs font-semibold tracking-wide flex items-center justify-center gap-2 transition-all pointer-events-auto ${
                activeTab === 'explorer'
                ? 'bg-slate-900 text-white shadow-sm'
                : 'hover:bg-slate-50 text-slate-550 hover:text-slate-900'
              }`}
              id="tab-btn-explorer"
            >
              <Code className="w-4 h-4" />
              Android Source Explorer
            </button>
            <button
              onClick={() => setActiveTab('configurator')}
              className={`flex-1 py-3 px-4 rounded-xl text-xs font-semibold tracking-wide flex items-center justify-center gap-2 transition-all pointer-events-auto ${
                activeTab === 'configurator'
                ? 'bg-slate-900 text-white shadow-sm'
                : 'hover:bg-slate-50 text-slate-550 hover:text-slate-900'
              }`}
              id="tab-btn-configurator"
            >
              <Sliders className="w-4 h-4" />
              Project Branding
            </button>
            <button
              onClick={() => setActiveTab('guide')}
              className={`flex-1 py-3 px-4 rounded-xl text-xs font-semibold tracking-wide flex items-center justify-center gap-2 transition-all pointer-events-auto ${
                activeTab === 'guide'
                ? 'bg-slate-900 text-white shadow-sm'
                : 'hover:bg-slate-50 text-slate-550 hover:text-slate-900'
              }`}
              id="tab-btn-guide"
            >
              <BookOpen className="w-4 h-4" />
              AndroidIDE Guide
            </button>
          </div>

          {/* Active Tab Panel Body */}
          <div className="bg-white p-6 rounded-3xl border border-slate-100 shadow-sm min-h-[460px] flex flex-col" id="active-panel-body">
            
            {activeTab === 'explorer' && (
              <div className="space-y-4 flex-1 flex flex-col" id="panel-explorer">
                <div className="select-none" id="explorer-intro">
                  <h3 className="text-base font-bold text-slate-900 flex items-center gap-1.5">
                    <Code className="w-5 h-5 text-slate-500" />
                    Android Source Woodwork
                  </h3>
                  <p className="text-xs text-slate-400 mt-1">
                    Edit the native Java files or Kotlin Gradle scripts directly. Hit "Save & Sync" to write changes to disk. They will sync automatically to AndroidIDE on your phone!
                  </p>
                </div>
                
                <CodeExplorer 
                  files={files} 
                  isLoading={isLoadingFiles}
                  onRefresh={fetchAndroidFiles}
                  onSaveFile={handleSaveFile}
                />
              </div>
            )}

            {activeTab === 'configurator' && (
              <div className="space-y-4" id="panel-configurator">
                <div className="select-none" id="branding-intro">
                  <h3 className="text-base font-bold text-slate-900 flex items-center gap-1.5">
                    <Sliders className="w-5 h-5 text-slate-500" />
                    Package Rebuilder & Refactorer
                  </h3>
                  <p className="text-xs text-slate-400 mt-1">
                    Easily rename your app, restructure package directory folders on the server, customize theme hex colors, and seed habit data with this visual builder.
                  </p>
                </div>
                
                <ProjectConfigurator 
                  config={config}
                  onUpdate={setConfig}
                  onApplyServerChanges={handleApplyServerChanges}
                />
              </div>
            )}

            {activeTab === 'guide' && (
              <div className="space-y-4" id="panel-guide">
                <div className="select-none" id="guide-intro">
                  <h3 className="text-base font-bold text-slate-900 flex items-center gap-1.5">
                    <BookOpen className="w-5 h-5 text-slate-500" />
                    AndroidIDE Installation manual
                  </h3>
                  <p className="text-xs text-slate-400 mt-1">
                    Learn how to pull this standard Android project tree and build a high-performance native app directly on your phone using AndroidIDE.
                  </p>
                </div>
                
                <SetupGuide />
              </div>
            )}

          </div>

          {/* Quick Warning / Tip Footer Card */}
          <div className="p-4 bg-slate-100 rounded-2xl border border-slate-200/50 flex gap-3 text-xs text-slate-500 leading-relaxed select-none" id="panel-warning-card">
            <AlertCircle className="w-5 h-5 text-slate-400 shrink-0 mt-0.5" />
            <div>
              <span className="font-semibold text-slate-700">Pro-Tip for AndroidIDE Developers:</span> Any directory restructuring performed in the <span className="font-medium text-slate-700">Project Branding</span> tab instantly rearranges the directories on disk matching standard Android packaging standards (<code className="bg-slate-200/80 px-1 rounded text-red-500">app/src/main/java/**/*</code>). Avoid manually changing folder layouts to avoid broken packages.
            </div>
          </div>
        </div>

      </main>

      {/* Footer copyright */}
      <footer className="text-center py-6 text-[11px] text-slate-400 select-none border-t border-slate-150/40 shrink-0" id="bottom-copyright-footer">
        <span>LifeFlow Premium is compatible with Android Lollipop API 21+ and compiled via Android Gradle Plugin 8.1.x</span>
      </footer>
    </div>
  );
}
