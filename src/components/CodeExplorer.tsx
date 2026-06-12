import React, { useState, useEffect } from 'react';
import { Save, FileCode, CheckCircle, RefreshCw, Folder, Loader2 } from 'lucide-react';
import { FileItem } from '../types';

interface CodeExplorerProps {
  files: FileItem[];
  isLoading: boolean;
  onRefresh: () => Promise<void>;
  onSaveFile: (filePath: string, content: string) => Promise<boolean>;
}

export default function CodeExplorer({ files, isLoading, onRefresh, onSaveFile }: CodeExplorerProps) {
  const [selectedFile, setSelectedFile] = useState<FileItem | null>(null);
  const [editedContent, setEditedContent] = useState("");
  const [isSaving, setIsSaving] = useState(false);
  const [saveStatus, setSaveStatus] = useState<'idle' | 'success' | 'error'>('idle');

  // Sync selected file contents on change
  useEffect(() => {
    if (files && files.length > 0) {
      if (!selectedFile) {
        // Default to MainActivity.java is preferred
        const mainActivity = files.find(f => f.name === "MainActivity.java") || files[0];
        setSelectedFile(mainActivity);
        setEditedContent(mainActivity.content);
      } else {
        // Ensure accurate synchronization if list was rebuilt underneath
        const updated = files.find(f => f.path === selectedFile.path);
        if (updated) {
          setSelectedFile(updated);
          setEditedContent(updated.content);
        }
      }
    }
  }, [files]);

  const selectFile = (file: FileItem) => {
    setSelectedFile(file);
    setEditedContent(file.content);
    setSaveStatus('idle');
  };

  const handleSave = async () => {
    if (!selectedFile) return;
    setIsSaving(true);
    setSaveStatus('idle');

    const success = await onSaveFile(selectedFile.path, editedContent);
    if (success) {
      setSaveStatus('success');
      setTimeout(() => setSaveStatus('idle'), 3000);
    } else {
      setSaveStatus('error');
    }
    setIsSaving(false);
  };

  return (
    <div className="flex flex-col md:flex-row border border-slate-100 rounded-2xl overflow-hidden bg-white text-slate-800" id="code-explorer-stage">
      
      {/* File Sidebar */}
      <div className="w-full md:w-64 bg-slate-50 border-r border-slate-100 flex flex-col shrink-0" id="sidebar-explorer">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between" id="sidebar-header">
          <span className="text-xs font-bold text-slate-400 uppercase tracking-widest flex items-center gap-1.5 select-none">
            <Folder className="w-4 h-4 text-slate-400" />
            File Tree
          </span>
          <button 
            type="button" 
            onClick={onRefresh} 
            disabled={isLoading}
            className="p-1.5 hover:bg-slate-100 rounded-md transition-colors pointer-events-auto"
            title="Refresh codebase from workspace"
            id="sidebar-btn-refresh"
          >
            <RefreshCw className={`w-3.5 h-3.5 text-slate-500 ${isLoading ? 'animate-spin' : ''}`} />
          </button>
        </div>

        <div className="divide-y divide-slate-100 max-h-[300px] md:max-h-[500px] overflow-y-auto" id="files-selector-list">
          {isLoading ? (
            <div className="flex items-center justify-center p-8 gap-2 text-slate-400 text-xs">
              <Loader2 className="w-4 h-4 animate-spin" />
              Scanning workspace files...
            </div>
          ) : files.length === 0 ? (
            <span className="text-xs text-slate-450 p-4 text-center block">No Java or Gradle files found.</span>
          ) : (
            files.map((file) => {
              const isActive = selectedFile?.path === file.path;
              return (
                <button
                  key={file.path}
                  onClick={() => selectFile(file)}
                  className={`w-full text-left p-3 flex gap-2.5 items-start text-xs font-mono transition-colors pointer-events-auto ${
                    isActive 
                    ? 'bg-slate-900 text-white' 
                    : 'hover:bg-slate-100 text-slate-600'
                  }`}
                  id={`sidebar-file-${file.name.toLowerCase().replace(/\./g, '-')}`}
                >
                  <FileCode className={`w-4 h-4 mt-0.5 shrink-0 ${isActive ? 'text-teal-400' : 'text-slate-400'}`} />
                  <div className="truncate">
                    <div className={`font-semibold ${isActive ? 'text-white' : 'text-slate-800'}`}>{file.name}</div>
                    <div className="text-[10px] text-slate-400 truncate mt-0.5" title={file.path}>{file.path}</div>
                  </div>
                </button>
              );
            })
          )}
        </div>
      </div>

      {/* Editor Surface */}
      <div className="flex-1 flex flex-col bg-slate-950 font-mono text-slate-300 min-h-[400px]" id="editor-workspace">
        {selectedFile ? (
          <>
            {/* Editor Top Bar */}
            <div className="px-4 py-3 bg-slate-900 border-b border-slate-950 flex items-center justify-between text-xs shrink-0 select-none" id="editor-toolbar">
              <span className="text-teal-400 font-mono text-[11px] truncate flex items-center gap-1.5 max-w-[200px] sm:max-w-xs">
                <FileCode className="w-4 h-4" />
                {selectedFile.path}
              </span>
              
              <div className="flex items-center gap-2">
                {saveStatus === 'success' && (
                  <span className="text-emerald-400 text-[11px] flex items-center gap-1">
                    <CheckCircle className="w-3.5 h-3.5" />
                    Saved to Disk
                  </span>
                )}
                {saveStatus === 'error' && (
                  <span className="text-rose-400 text-[11px]">
                    Save Failed
                  </span>
                )}
                
                <button
                  onClick={handleSave}
                  disabled={isSaving}
                  className="bg-teal-500 hover:bg-teal-400 active:scale-95 disabled:scale-100 text-slate-950 font-semibold rounded-md py-1.5 px-3 select-none transition-all flex items-center gap-1 text-[11px] disabled:opacity-50 pointer-events-auto"
                  id="editor-btn-save"
                >
                  {isSaving ? (
                    <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                  ) : (
                    <Save className="w-3.5 h-3.5" />
                  )}
                  Save & Sync
                </button>
              </div>
            </div>

            {/* Editing Box */}
            <div className="flex-1 relative flex" id="textarea-editor-field">
              {/* Fake line counters */}
              <div className="w-10 bg-slate-900/60 text-slate-600/70 py-4 text-right pr-2 text-[11px] font-mono select-none border-r border-slate-900/10 shrink-0">
                {Array.from({ length: 45 }, (_, i) => (
                  <div key={i}>{i + 1}</div>
                ))}
                <div>...</div>
              </div>

              <textarea
                className="flex-1 bg-transparent p-4 text-[11px] leading-relaxed text-slate-300 focus:outline-none focus:ring-0 font-mono resize-none h-[400px] overflow-y-auto no-scrollbar"
                value={editedContent}
                onChange={(e) => setEditedContent(e.target.value)}
                spellCheck="false"
                id="active-editor-textarea"
              />
            </div>
          </>
        ) : (
          <div className="flex-1 flex flex-col items-center justify-center text-slate-500 gap-2 font-sans select-none text-xs" id="editor-placeholder">
            <FileCode className="w-8 h-8 text-slate-700 animate-pulse" />
            <span>Select any Gradle resource or Java class to customize.</span>
          </div>
        )}
      </div>

    </div>
  );
}
