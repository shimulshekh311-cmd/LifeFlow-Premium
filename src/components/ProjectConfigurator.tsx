import React, { useState } from 'react';
import { Sliders, Sparkles, FolderSync, RefreshCw, Check, AlertCircle } from 'lucide-react';
import { ProjectConfig } from '../types';

interface ProjectConfiguratorProps {
  config: ProjectConfig;
  onUpdate: (newConfig: ProjectConfig) => void;
  onApplyServerChanges: (newConfig: ProjectConfig) => Promise<{ success: boolean; message: string; currentPackage: string }>;
}

const PRESETS = [
  { name: "Forest Zen (Default)", primary: "#0F172A", accent: "#10B981" },
  { name: "Deep Royal Dream", primary: "#1E1B4B", accent: "#8B5CF6" },
  { name: "Crimson Heart", primary: "#450A0A", accent: "#EF4444" },
  { name: "Nordic Charcoal", primary: "#18181B", accent: "#06B6D4" }
];

export default function ProjectConfigurator({ config, onUpdate, onApplyServerChanges }: ProjectConfiguratorProps) {
  const [appName, setAppName] = useState(config.appName);
  const [packageName, setPackageName] = useState(config.packageName);
  const [primaryColor, setPrimaryColor] = useState(config.primaryColor);
  const [accentColor, setAccentColor] = useState(config.accentColor);
  const [seedHabitsInput, setSeedHabitsInput] = useState(config.seedHabits.join("\n"));
  
  const [isApplying, setIsApplying] = useState(false);
  const [notification, setNotification] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  const selectPreset = (preset: typeof PRESETS[0]) => {
    setPrimaryColor(preset.primary);
    setAccentColor(preset.accent);
    
    onUpdate({
      ...config,
      primaryColor: preset.primary,
      accentColor: preset.accent
    });
  };

  const notify = (type: 'success' | 'error', message: string) => {
    setNotification({ type, message });
    setTimeout(() => {
      setNotification(null);
    }, 5000);
  };

  const handleApply = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsApplying(true);
    setNotification(null);

    const habitsArray = seedHabitsInput
      .split("\n")
      .map(h => h.trim())
      .filter(h => h.length > 0);

    const updatedConfig: ProjectConfig = {
      appName,
      packageName: packageName.trim().toLowerCase(),
      primaryColor,
      accentColor,
      seedHabits: habitsArray
    };

    try {
      const response = await onApplyServerChanges(updatedConfig);
      if (response.success) {
        onUpdate(updatedConfig);
        setPackageName(response.currentPackage); // Use clean name returned from server
        notify('success', response.message || "Android files updated and package folders successfully restructured!");
      } else {
        notify('error', "Failed to save configurations: " + response.message);
      }
    } catch (err: any) {
      notify('error', "Server error: " + err.message);
    } finally {
      setIsApplying(false);
    }
  };

  return (
    <form onSubmit={handleApply} className="space-y-6 text-slate-800" id="configurator-form">
      {notification && (
        <div 
          className={`p-4 rounded-xl border flex items-start gap-2 text-sm ${
            notification.type === 'success' 
            ? 'bg-emerald-50 border-emerald-200 text-emerald-800' 
            : 'bg-rose-50 border-rose-200 text-rose-800'
          }`}
          id="configurator-notification"
        >
          {notification.type === 'success' ? <Check className="w-5 h-5 shrink-0" /> : <AlertCircle className="w-5 h-5 shrink-0" />}
          <div>{notification.message}</div>
        </div>
      )}

      {/* App details */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4" id="config-text-fields">
        <div>
          <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Android App Name</label>
          <input
            type="text"
            className="w-full bg-slate-50 border border-slate-200 focus:border-slate-400 focus:bg-white text-slate-900 rounded-xl px-4 py-3 text-sm transition-all focus:outline-none"
            value={appName}
            onChange={(e) => {
              setAppName(e.target.value);
              onUpdate({ ...config, appName: e.target.value });
            }}
            required
            id="cfg-input-appname"
          />
        </div>
        <div>
          <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Package Name (ApplicationID)</label>
          <input
            type="text"
            className="w-full bg-slate-50 border border-slate-200 focus:border-slate-400 focus:bg-white font-mono text-xs text-slate-900 rounded-xl px-4 py-3 transition-all focus:outline-none"
            value={packageName}
            onChange={(e) => setPackageName(e.target.value)}
            pattern="^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$"
            title="Must be a valid Android package format, e.g. com.example.myapp"
            required
            id="cfg-input-packagename"
          />
        </div>
      </div>

      {/* Presets */}
      <div id="theme-presets-section">
        <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Color Presets (Themes)</label>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2" id="presets-grid">
          {PRESETS.map((p) => {
            const isActive = primaryColor.toLowerCase() === p.primary.toLowerCase() && accentColor.toLowerCase() === p.accent.toLowerCase();
            return (
              <button
                key={p.name}
                type="button"
                onClick={() => selectPreset(p)}
                className={`flex flex-col p-3 rounded-xl border transition-all text-left pointer-events-auto ${
                  isActive 
                  ? 'border-slate-900 bg-slate-50 shadow-sm' 
                  : 'border-slate-100 hover:border-slate-200 bg-white'
                }`}
                id={`preset-btn-${p.name.replace(/\s+/g, '-').toLowerCase()}`}
              >
                <span className="text-xs font-medium text-slate-800 line-clamp-1">{p.name}</span>
                <div className="flex gap-1.5 mt-2">
                  <div className="w-5 h-5 rounded-full border border-slate-200" style={{ backgroundColor: p.primary }} />
                  <div className="w-5 h-5 rounded-full border border-slate-200" style={{ backgroundColor: p.accent }} />
                </div>
              </button>
            );
          })}
        </div>
      </div>

      {/* Raw Hex Colors Picker */}
      <div className="grid grid-cols-2 gap-4" id="custom-color-pickers">
        <div>
          <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Primary Slate Hex</label>
          <div className="flex gap-2">
            <input
              type="color"
              className="w-10 h-10 rounded-lg cursor-pointer border border-slate-200 bg-transparent shrink-0 pointer-events-auto"
              value={primaryColor}
              onChange={(e) => {
                setPrimaryColor(e.target.value);
                onUpdate({ ...config, primaryColor: e.target.value });
              }}
              id="cfg-color-picker-primary"
            />
            <input
              type="text"
              maxLength={7}
              placeholder="#000000"
              className="w-full bg-slate-50 border border-slate-200 text-slate-800 font-mono text-sm px-3 rounded-lg focus:outline-none focus:border-slate-400"
              value={primaryColor}
              onChange={(e) => {
                setPrimaryColor(e.target.value);
                onUpdate({ ...config, primaryColor: e.target.value });
              }}
              id="cfg-input-hex-primary"
            />
          </div>
        </div>
        <div>
          <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Accent Emerald Hex</label>
          <div className="flex gap-2">
            <input
              type="color"
              className="w-10 h-10 rounded-lg cursor-pointer border border-slate-200 bg-transparent shrink-0 pointer-events-auto"
              value={accentColor}
              onChange={(e) => {
                setAccentColor(e.target.value);
                onUpdate({ ...config, accentColor: e.target.value });
              }}
              id="cfg-color-picker-accent"
            />
            <input
              type="text"
              maxLength={7}
              placeholder="#000000"
              className="w-full bg-slate-50 border border-slate-200 text-slate-800 font-mono text-sm px-3 rounded-lg focus:outline-none focus:border-slate-400"
              value={accentColor}
              onChange={(e) => {
                setAccentColor(e.target.value);
                onUpdate({ ...config, accentColor: e.target.value });
              }}
              id="cfg-input-hex-accent"
            />
          </div>
        </div>
      </div>

      {/* Starting Habits List */}
      <div id="seed-habits-section">
        <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Starting Pre-loaded Habits (One per line)</label>
        <textarea
          rows={4}
          className="w-full bg-slate-50 border border-slate-200 focus:border-slate-400 focus:bg-white text-slate-900 rounded-xl px-4 py-3 text-sm font-sans focus:outline-none transition-all resize-none"
          value={seedHabitsInput}
          onChange={(e) => setSeedHabitsInput(e.target.value)}
          placeholder="Drink warm water&#10;Inhale deeply&#10;Walk for 30 minutes"
          id="cfg-textarea-habits"
        />
        <p className="text-[11px] text-slate-400 mt-1">
          When the app boots in Android on first run, it inserts these values natively into the local SQLite table.
        </p>
      </div>

      <div className="border-t border-slate-100 pt-4" id="config-actions">
        <button
          type="submit"
          disabled={isApplying}
          className="w-full bg-slate-900 hover:bg-slate-800 text-white font-semibold rounded-xl py-3.5 px-4 text-sm transition-all shadow-sm flex items-center justify-center gap-2 select-none disabled:opacity-50 pointer-events-auto"
          id="cfg-btn-submit"
        >
          {isApplying ? (
            <>
              <RefreshCw className="w-4 h-4 animate-spin" />
              Restructuring Workspace Folders...
            </>
          ) : (
            <>
              <FolderSync className="w-4 h-4" />
              Sync & Clean Restructure Files
            </>
          )}
        </button>
      </div>
    </form>
  );
}
