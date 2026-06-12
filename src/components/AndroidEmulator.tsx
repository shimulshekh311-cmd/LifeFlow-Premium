import React, { useState, useEffect } from 'react';
import { Plus, Trash2, Droplet, Flame, Wind, MessageSquare, Terminal, RefreshCw, Layers } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { ProjectConfig, HabitItem } from '../types';

interface AndroidEmulatorProps {
  config: ProjectConfig;
}

export default function AndroidEmulator({ config }: AndroidEmulatorProps) {
  const [habits, setHabits] = useState<HabitItem[]>([
    { id: 1, title: "Drink warm lemon water", completed: false },
    { id: 2, title: "5-min mindful breathing stretch", completed: true },
    { id: 3, title: "Journal 3 things I'm grateful for", completed: false }
  ]);
  const [newHabitTitle, setNewHabitTitle] = useState("");
  const [waterCountMl, setWaterCountMl] = useState(1000);
  const [streakCount, setStreakCount] = useState(5);
  
  // Breathing animation states
  const [isBreathing, setIsBreathing] = useState(false);
  const [breathText, setBreathText] = useState("Tap to start 1-min breath reset");
  const [breathPhase, setBreathPhase] = useState<'idle' | 'inhale' | 'exhale'>('idle');

  // SQLite execution logs feed
  const [dbLogs, setDbLogs] = useState<{ id: string; query: string; timestamp: string }[]>([
    { id: '1', query: "sqlite> CREATE TABLE habits (id INTEGER PRIMARY KEY, title TEXT, completed INTEGER DEFAULT 0);", timestamp: "02:30:10" },
    { id: '2', query: "sqlite> INSERT INTO habits (title, completed) VALUES ('Drink warm lemon water', 0);", timestamp: "02:30:11" },
    { id: '3', query: "sqlite> INSERT INTO habits (title, completed) VALUES ('5-min mindful breathing stretch', 1);", timestamp: "02:30:11" },
    { id: '4', query: "sqlite> SELECT * FROM habits ORDER BY id ASC;", timestamp: "02:30:12" }
  ]);

  const addLog = (query: string) => {
    const timeStr = new Date().toTimeString().split(' ')[0];
    setDbLogs(prev => [
      ...prev,
      { id: Date.now().toString(), query, timestamp: timeStr }
    ].slice(-5)); // keep last 5
  };

  const handleAddHabit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newHabitTitle.trim()) return;
    
    const newId = Date.now();
    const cleanTitle = newHabitTitle.trim();
    setHabits(prev => [...prev, { id: newId, title: cleanTitle, completed: false }]);
    setNewHabitTitle("");
    
    addLog(`sqlite> INSERT INTO habits (title, completed) VALUES ('${cleanTitle.replace(/'/g, "''")}', 0);`);
    addLog(`sqlite> SELECT * FROM habits; -- list refreshed`);
  };

  const handleToggleHabit = (id: number, currentCompleted: boolean) => {
    setHabits(prev => 
      prev.map(h => h.id === id ? { ...h, completed: !currentCompleted } : h)
    );
    addLog(`sqlite> UPDATE habits SET completed = ${!currentCompleted ? 1 : 0} WHERE id = ${id};`);
  };

  const handleDeleteHabit = (id: number, title: string) => {
    setHabits(prev => prev.filter(h => h.id !== id));
    addLog(`sqlite> DELETE FROM habits WHERE id = ${id}; -- Deleted: "${title}"`);
  };

  const incrementWater = () => {
    setWaterCountMl(prev => {
      const next = prev + 250;
      addLog(`sqlite> -- Session cache: updated water metric to ${next}ml`);
      return next;
    });
  };

  const decrementWater = () => {
    setWaterCountMl(prev => {
      if (prev <= 0) return 0;
      const next = prev - 250;
      addLog(`sqlite> -- Session cache: updated water metric to ${next}ml`);
      return next;
    });
  };

  // Breathing countdown simulation
  useEffect(() => {
    let timer: NodeJS.Timeout;
    let secondsLeft = 12; // Shortened for fast web preview demonstration
    let cycle = 0;

    if (isBreathing) {
      setStreakCount(s => s + 1);
      setBreathPhase('inhale');
      setBreathText("Innale deeply... (3s)");
      
      timer = setInterval(() => {
        cycle++;
        if (cycle >= 4) {
          setIsBreathing(false);
          setBreathPhase('idle');
          setBreathText("Mindfully centered! Daily streak bumped 🔥");
          addLog("sqlite> -- Broadcast: streak increment triggers updated user record.");
        } else if (cycle % 2 === 0) {
          setBreathPhase('inhale');
          setBreathText("Inhale deeply... 🧘‍♂️");
        } else {
          setBreathPhase('exhale');
          setBreathText("Exhale slowly... 🌬️");
        }
      }, 3000);
    } else {
      setBreathPhase('idle');
      setBreathText("Tap 'Start Breaths' for 12s focus session");
    }

    return () => clearInterval(timer);
  }, [isBreathing]);

  return (
    <div className="flex flex-col items-center justify-center p-2" id="android-emulator-pane">
      {/* Phone Case */}
      <div 
        className="relative w-[340px] h-[680px] bg-slate-950 rounded-[40px] shadow-2xl border-[10px] border-slate-900 flex flex-col overflow-hidden ring-[1px] ring-slate-800"
        id="phone-frame-mockup"
      >
        {/* Notch */}
        <div className="absolute top-0 inset-x-0 h-5 flex justify-center z-50 pointer-events-none" id="phone-notch">
          <div className="w-28 h-4 bg-slate-900 rounded-b-xl flex items-center justify-around px-4">
            <div className="w-2.5 h-2.5 rounded-full bg-slate-850 border border-slate-950 shrink-0" />
            <div className="w-12 h-1 bg-slate-800 rounded" />
          </div>
        </div>

        {/* Outer Phone Status Bar */}
        <div className="h-6 bg-slate-950 flex items-center justify-between px-6 pt-1 text-[10px] text-white/70 font-mono font-medium shrink-0 z-40 select-none">
          <span>09:41</span>
          <div className="flex items-center gap-2">
            <span>5G</span>
            <div className="w-5 h-2.5 border border-white/40 rounded-sm p-[1px] flex">
              <div className="bg-emerald-400 h-full w-[80%] rounded-xs" />
            </div>
          </div>
        </div>

        {/* Primary Screen Area */}
        <div className="flex-1 bg-slate-50 flex flex-col overflow-y-auto no-scrollbar relative" id="phone-content-area">
          {/* Header */}
          <div 
            className="p-6 pb-8 text-white rounded-b-[24px] shadow-sm shrink-0 transition-colors duration-550"
            style={{ backgroundColor: config.primaryColor }}
            id="phone-app-header"
          >
            <div className="flex items-center justify-between mt-2">
              <div>
                <span className="text-[11px] text-white/60 tracking-widest uppercase font-semibold">Welcome Back</span>
                <h2 className="text-xl font-bold tracking-tight mt-0.5">{config.appName}</h2>
              </div>
              <div className="w-9 h-9 rounded-full bg-white/10 flex items-center justify-center font-bold text-sm text-center border border-white/5 shadow-inner">
                LF
              </div>
            </div>
            <p className="text-xs text-white/70 italic mt-3 font-sans opacity-90">
              "Quiet the mind, find your flow."
            </p>
          </div>

          {/* Interactive Screen Components */}
          <div className="p-4 space-y-4 flex-1 pb-16" id="phone-activity-screen">
            {/* Streak card */}
            <div className="bg-white p-4 rounded-2xl border border-slate-100 flex items-center justify-between shadow-xs" id="phone-streak-card">
              <div className="space-y-0.5 animate-pulse">
                <span className="text-[10px] text-slate-400 font-semibold uppercase tracking-wider">MORTAL PROGRESS</span>
                <h4 className="text-sm font-bold text-slate-800 flex items-center gap-1.5">
                  <Flame className="w-4 h-4 text-orange-500 fill-orange-500" />
                  Streak: {streakCount} Days
                </h4>
              </div>
              <span className="text-slate-800 text-2xl font-bold">🔥</span>
            </div>

            {/* Hydration Card */}
            <div className="bg-white p-4 rounded-2xl border border-slate-100 space-y-3 shadow-xs" id="phone-water-card">
              <div className="flex items-center justify-between">
                <h4 className="text-xs font-bold text-slate-700 flex items-center gap-1.5 uppercase tracking-wide">
                  <Droplet className="w-4 h-4 text-sky-500 fill-sky-500" />
                  Daily Water Tracker
                </h4>
                <span className="text-[11px] font-mono text-sky-600 bg-sky-50 px-2 py-0.5 rounded-full font-bold">80% Target</span>
              </div>
              
              <div className="flex items-center justify-between mt-2">
                <span className="text-xs font-semibold text-slate-800">{waterCountMl} ml</span>
                <div className="flex gap-2">
                  <button 
                    onClick={decrementWater}
                    className="w-8 h-8 rounded-lg bg-sky-50 hover:bg-sky-100/80 active:scale-95 text-sky-600 flex items-center justify-center transition-all border border-sky-100 font-bold text-sm pointer-events-auto"
                    id="phone-btn-water-minus"
                  >
                    -
                  </button>
                  <button 
                    onClick={incrementWater}
                    className="w-8 h-8 rounded-lg bg-sky-50 hover:bg-sky-100/80 active:scale-95 text-sky-600 flex items-center justify-center transition-all border border-sky-100 font-bold text-sm pointer-events-auto"
                    id="phone-btn-water-plus"
                  >
                    +
                  </button>
                </div>
              </div>
            </div>

            {/* Breathing Simulator */}
            <div className="bg-white p-4 rounded-2xl border border-slate-100 space-y-3 shadow-xs flex flex-col items-center justify-center text-center" id="phone-breath-card">
              <h4 className="text-xs font-bold text-slate-700 flex items-center gap-1.5 uppercase self-start tracking-wide">
                <Wind className="w-4 h-4 text-emerald-500" />
                Breathing Assistant
              </h4>
              
              {/* Pulsing Breathing circle */}
              <div className="relative w-20 h-20 my-2 flex items-center justify-center">
                <AnimatePresence mode="popLayout">
                  {breathPhase === 'inhale' && (
                    <motion.div 
                      key="inhale-ring"
                      initial={{ scale: 0.8, opacity: 0.2 }}
                      animate={{ scale: 1.4, opacity: 0.6 }}
                      exit={{ opacity: 0 }}
                      transition={{ repeat: Infinity, duration: 3, ease: 'easeInOut' }}
                      className="absolute inset-0 rounded-full bg-emerald-100"
                    />
                  )}
                  {breathPhase === 'exhale' && (
                    <motion.div 
                      key="exhale-ring"
                      initial={{ scale: 1.4, opacity: 0.6 }}
                      animate={{ scale: 0.8, opacity: 0.2 }}
                      exit={{ opacity: 0 }}
                      transition={{ repeat: Infinity, duration: 3, ease: 'easeInOut' }}
                      className="absolute inset-0 rounded-full bg-teal-100"
                    />
                  )}
                </AnimatePresence>
                
                <div 
                  className="w-14 h-14 rounded-full flex items-center justify-center text-white font-bold transition-all duration-300 shadow-inner"
                  style={{ backgroundColor: config.accentColor }}
                >
                  <Wind className={`w-6 h-6 ${isBreathing ? 'animate-spin' : ''}`} />
                </div>
              </div>

              <span className="text-xs font-medium text-slate-500 mt-1 block max-w-[200px]" id="phone-breath-label">
                {breathText}
              </span>

              <button
                onClick={() => setIsBreathing(prev => !prev)}
                className="w-full text-xs font-bold py-2 px-3 rounded-lg text-white font-sans transition-all active:scale-98 shadow-sm pointer-events-auto"
                style={{ backgroundColor: isBreathing ? '#EF4444' : config.accentColor }}
                id="phone-btn-breath-trigger"
              >
                {isBreathing ? "Cancel Session" : "Start 12s Focus"}
              </button>
            </div>

            {/* List Habits */}
            <div className="space-y-2 mt-4" id="phone-habits-list-section">
              <h3 className="text-xs font-bold text-slate-500 uppercase tracking-widest pl-1">Daily Habits</h3>
              
              <form onSubmit={handleAddHabit} className="flex gap-2" id="phone-add-habit-form">
                <input
                  type="text"
                  placeholder="Enter custom daily habit..."
                  className="bg-white border border-slate-200 focus:outline-none focus:border-slate-400 placeholder:text-slate-300 text-xs rounded-xl px-3 h-9 flex-1 text-slate-800"
                  value={newHabitTitle}
                  onChange={(e) => setNewHabitTitle(e.target.value)}
                  id="phone-input-habit"
                />
                <button
                  type="submit"
                  className="h-9 w-9 bg-slate-900 border border-slate-900 text-white rounded-xl flex items-center justify-center hover:bg-slate-800 active:scale-95 pointer-events-auto"
                  id="phone-btn-habit-add"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </form>

              <div className="space-y-1.5" id="phone-habits-container">
                {habits.map((habit) => (
                  <div 
                    key={habit.id}
                    className="flex items-center gap-2 bg-white p-3 rounded-xl border border-slate-100 hover:border-slate-200 transition-all shadow-2xs group"
                    id={`phone-habit-item-${habit.id}`}
                  >
                    <input
                      type="checkbox"
                      checked={habit.completed}
                      onChange={() => handleToggleHabit(habit.id, habit.completed)}
                      className="w-4.5 h-4.5 rounded text-emerald-500 border-slate-300 focus:ring-emerald-400 cursor-pointer pointer-events-auto"
                    />
                    <span className={`text-xs flex-1 ${habit.completed ? 'line-through text-slate-300' : 'text-slate-700 font-medium'}`}>
                      {habit.title}
                    </span>
                    <button 
                      onClick={() => handleDeleteHabit(habit.id, habit.title)}
                      className="text-slate-300 hover:text-rose-500 p-1 rounded-md hover:bg-slate-50 transition-colors pointer-events-auto"
                      id={`phone-btn-habit-del-${habit.id}`}
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Home Indicator */}
        <div className="absolute bottom-1 inset-x-0 h-4 flex items-center justify-center z-50 pointer-events-none" id="phone-home-pill-container">
          <div className="w-32 h-1 bg-white/70 rounded-full" />
        </div>
      </div>

      {/* Database engine query feed */}
      <div className="w-full max-w-[340px] mt-4 bg-slate-950 rounded-2xl p-4 border border-slate-900 shadow-inner" id="sqlite-console-log-box">
        <h5 className="text-[10px] uppercase font-bold text-slate-500 tracking-wider flex items-center gap-1.5 select-none font-sans mb-2 border-b border-white/5 pb-1.5">
          <Terminal className="w-3.5 h-3.5 text-lime-400" />
          Native SQLite Database Output
        </h5>
        
        <div className="font-mono text-[10px] text-lime-400/90 leading-relaxed space-y-1.5 min-h-[90px] max-h-[140px] overflow-y-auto overflow-x-hidden" id="sqlite-logs-scroller">
          {dbLogs.map((log) => (
            <div key={log.id} className="flex gap-1 items-start" id={`sqlite-log-${log.id}`}>
              <span className="text-white/30 text-[9px] select-none shrink-0">{log.timestamp}</span>
              <span className="break-all whitespace-pre-wrap">{log.query}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
