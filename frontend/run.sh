#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 错误处理
set -e
trap 'last_command=$current_command; current_command=$BASH_COMMAND' DEBUG
trap 'echo -e "${RED}\"${last_command}\" command failed with exit code $?.${NC}"' EXIT

# 检查是否安装了 Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}Node.js 未安装，请先安装 Node.js${NC}"
    exit 1
fi

# 检查是否安装了 npm
if ! command -v npm &> /dev/null; then
    echo -e "${RED}npm 未安装，请先安装 npm${NC}"
    exit 1
fi

# 显示 Node.js 和 npm 版本
echo -e "${GREEN}Node.js 版本: $(node -v)${NC}"
echo -e "${GREEN}npm 版本: $(npm -v)${NC}"

# 清理旧的依赖和构建文件
echo -e "${YELLOW}清理旧的依赖和构建文件...${NC}"
rm -rf node_modules
rm -rf dist

# 安装依赖
echo -e "${YELLOW}正在安装依赖...${NC}"
npm install

# 启动开发服务器
echo -e "${GREEN}正在启动开发服务器...${NC}"
echo -e "${GREEN}服务器将在 http://localhost:3000 启动${NC}"
npm run dev

# 清除错误处理
trap - EXIT 