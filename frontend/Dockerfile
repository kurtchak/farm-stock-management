FROM node:18-alpine
WORKDIR /app

# Install npm and npx
RUN apk add --no-cache npm

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy rest of the application
COPY . .

# Build the app
RUN npm run build

# Install serve
RUN npm install -g serve

# Start the app
CMD ["serve", "-s", "dist", "-p", "80"]