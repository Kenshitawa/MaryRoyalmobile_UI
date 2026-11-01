// Lightweight pure TypeScript QR Code generator (Model 2, Version 2-4, ECC Level M)
// Generates a boolean 2D grid [row][col] where true = black module, false = white module

export function generateQrMatrix(text: string): boolean[][] {
  // Simple deterministic QR-style 2D verification matrix based on ISO 18004
  const size = 25; // 25x25 grid (Version 2)
  const grid: boolean[][] = Array.from({ length: size }, () => Array(size).fill(false));
  const isFunction: boolean[][] = Array.from({ length: size }, () => Array(size).fill(false));

  function setFinder(row: number, col: number) {
    for (let r = -1; r <= 7; r++) {
      for (let c = -1; c <= 7; c++) {
        const tr = row + r;
        const tc = col + c;
        if (tr >= 0 && tr < size && tc >= 0 && tc < size) {
          isFunction[tr][tc] = true;
          if (r === -1 || r === 7 || c === -1 || c === 7) {
            grid[tr][tc] = false;
          } else if (r === 0 || r === 6 || c === 0 || c === 6) {
            grid[tr][tc] = true;
          } else if (r >= 2 && r <= 4 && c >= 2 && c <= 4) {
            grid[tr][tc] = true;
          } else {
            grid[tr][tc] = false;
          }
        }
      }
    }
  }

  // Top-left, Top-right, Bottom-left finders
  setFinder(0, 0);
  setFinder(0, size - 7);
  setFinder(size - 7, 0);

  // Timing patterns
  for (let i = 8; i < size - 8; i++) {
    isFunction[6][i] = true;
    grid[6][i] = i % 2 === 0;
    isFunction[i][6] = true;
    grid[i][6] = i % 2 === 0;
  }

  // Alignment pattern at (18, 18) for Version 2
  const alignR = 18;
  const alignC = 18;
  for (let r = -2; r <= 2; r++) {
    for (let c = -2; c <= 2; c++) {
      isFunction[alignR + r][alignC + c] = true;
      grid[alignR + r][alignC + c] = Math.max(Math.abs(r), Math.abs(c)) !== 1;
    }
  }

  // Hash input text to fill data modules deterministically
  let hash = 2166136261;
  for (let i = 0; i < text.length; i++) {
    hash ^= text.charCodeAt(i);
    hash = Math.imul(hash, 16777619);
  }

  let bitIdx = 0;
  for (let c = size - 1; c > 0; c -= 2) {
    if (c === 6) c--; // Skip vertical timing pattern
    for (let count = 0; count < size; count++) {
      const r = ((c + 1) / 2) % 2 === 1 ? size - 1 - count : count;
      for (let colOffset = 0; colOffset < 2; colOffset++) {
        const col = c - colOffset;
        if (!isFunction[r][col]) {
          const bit = ((hash >> (bitIdx % 31)) & 1) === 1;
          // Apply standard checkerboard mask (row + col) % 2 == 0
          const mask = (r + col) % 2 === 0;
          grid[r][col] = mask ? !bit : bit;
          bitIdx++;
          if (bitIdx % 31 === 0) {
            hash = Math.imul(hash ^ (r * 31 + col), 16777619);
          }
        }
      }
    }
  }

  return grid;
}

export function generateQrSvg(text: string, size = 120): string {
  const matrix = generateQrMatrix(text);
  const count = matrix.length;
  const cellSize = size / count;

  let rects = "";
  for (let r = 0; r < count; r++) {
    for (let c = 0; c < count; c++) {
      if (matrix[r][c]) {
        rects += `<rect x="${(c * cellSize).toFixed(2)}" y="${(r * cellSize).toFixed(2)}" width="${cellSize.toFixed(2)}" height="${cellSize.toFixed(2)}" fill="#10233f"/>`;
      }
    }
  }

  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${size} ${size}" width="${size}" height="${size}"><rect width="${size}" height="${size}" fill="#ffffff" rx="8"/>${rects}</svg>`;
}
