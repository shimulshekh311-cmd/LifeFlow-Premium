import React from 'react';
import { Smartphone, Download, CheckCircle, Wifi, Terminal, ExternalLink } from 'lucide-react';

export default function SetupGuide() {
  return (
    <div className="space-y-6 text-slate-800" id="setup-guide-container">
      <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-xl" id="guide-overview">
        <h3 className="font-semibold text-emerald-900 flex items-center gap-2">
          <CheckCircle className="w-5 h-5" />
          AndroidIDE Ready Workspace
        </h3>
        <p className="text-sm text-emerald-800 mt-1">
          This workspace is preconfigured with standard Gradle Kotlin build files (<code className="bg-emerald-100 px-1 rounded">.kts</code>), modern resources, and SQLite native database helper configurations. It's ready to compile immediately in AndroidIDE.
        </p>
      </div>

      <div className="space-y-4" id="guide-steps-list">
        <h4 className="font-bold text-slate-900 text-base">How to compile on your Android device:</h4>
        
        {/* Step 1 */}
        <div className="flex gap-4 items-start" id="step-1">
          <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-700 shrink-0 select-none">
            1
          </div>
          <div>
            <h5 className="font-semibold text-slate-900 text-sm">Download Workspace ZIP</h5>
            <p className="text-xs text-slate-500 mt-1">
              Export this project by clicking the <span className="font-medium text-slate-700">Settings Icon</span> at the top-right of AI Studio and selecting <span className="font-medium text-slate-700">Export as ZIP</span> or <span className="font-medium text-slate-700">Push to GitHub</span>.
            </p>
          </div>
        </div>

        {/* Step 2 */}
        <div className="flex gap-4 items-start" id="step-2">
          <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-700 shrink-0 select-none">
            2
          </div>
          <div>
            <h5 className="font-semibold text-slate-900 text-sm">Transfer to Android Device</h5>
            <p className="text-xs text-slate-500 mt-1">
              Copy the downloaded ZIP archive directly to your Android phone, or clone the repository via Git inside your preferred file manager. Extract it inside the AndroidIDE Projects directory:
            </p>
            <div className="bg-slate-900 text-slate-300 font-mono text-[11px] p-2 rounded-md mt-2 border border-slate-800 select-all">
              /storage/emulated/0/AndroidIDEProjects/LifeFlowPremium
            </div>
          </div>
        </div>

        {/* Step 3 */}
        <div className="flex gap-4 items-start" id="step-3">
          <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-700 shrink-0 select-none">
            3
          </div>
          <div>
            <h5 className="font-semibold text-slate-900 text-sm">Open in AndroidIDE</h5>
            <p className="text-xs text-slate-500 mt-1">
              Fire up your <span className="font-medium text-slate-800">AndroidIDE app</span>. Tap <span className="font-medium text-slate-800">Open Project</span> and select the extracted folder. AndroidIDE will scan the Kotlin Gradle build script, resolve local dependencies, and synchronize the codebase.
            </p>
          </div>
        </div>

        {/* Step 4 */}
        <div className="flex gap-4 items-start" id="step-4">
          <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center font-bold text-slate-700 shrink-0 select-none">
            4
          </div>
          <div>
            <h5 className="font-semibold text-slate-900 text-sm">Compile and Install APK</h5>
            <p className="text-xs text-slate-500 mt-1">
              Click the <span className="font-medium text-slate-800">Build/Run</span> button inside AndroidIDE. It executes standard daemon compile processes natively on your phone and prompts you for immediate package installation.
            </p>
          </div>
        </div>
      </div>

      <div className="border-t border-slate-100 pt-4 mt-6" id="guide-resources-section">
        <h4 className="font-semibold text-xs text-slate-400 uppercase tracking-widest mb-3">Useful Links & Troubleshooting</h4>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2" id="links-grid">
          <a
            href="https://androidide.com/"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-between p-3 bg-slate-50 hover:bg-slate-100 rounded-xl border border-slate-100 transition-colors pointer-events-auto"
            id="url-androidide-home"
          >
            <div className="flex items-center gap-2">
              <Smartphone className="w-4 h-4 text-slate-500" />
              <span className="text-xs font-medium text-slate-700">Official AndroidIDE Manual</span>
            </div>
            <ExternalLink className="w-3.5 h-3.5 text-slate-400" />
          </a>
          <a
            href="https://github.com/AndroidIDE-Official/AndroidIDE"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-between p-3 bg-slate-50 hover:bg-slate-100 rounded-xl border border-slate-100 transition-colors pointer-events-auto"
            id="url-androidide-git"
          >
            <div className="flex items-center gap-2">
              <Terminal className="w-4 h-4 text-slate-500" />
              <span className="text-xs font-medium text-slate-700">AndroidIDE Github Repository</span>
            </div>
            <ExternalLink className="w-3.5 h-3.5 text-slate-400" />
          </a>
        </div>
      </div>
    </div>
  );
}
