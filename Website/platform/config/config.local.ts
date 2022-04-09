import {defineConfig} from 'umi';

export default defineConfig({
  define:{
    'process.env.platform': 'http://127.0.0.1:5010',
    'process.env.file': 'http://127.0.0.1:5010',
  },
});
