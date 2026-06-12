export interface FileItem {
  path: string;
  name: string;
  content: string;
}

export interface ProjectConfig {
  appName: string;
  packageName: string;
  primaryColor: string;
  accentColor: string;
  seedHabits: string[];
}

export interface HabitItem {
  id: number;
  title: string;
  completed: boolean;
}
