const fs = require('fs');
const path = require('path');

const tabbarDir = path.join(__dirname, '../static/tabbar');

const createPNG = (width, height, r, g, b, a = 255) => {
  const signature = Buffer.from([0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A]);
  
  const createChunk = (type, data) => {
    const length = Buffer.alloc(4);
    length.writeUInt32BE(data.length, 0);
    
    const typeBuffer = Buffer.from(type);
    const crcData = Buffer.concat([typeBuffer, data]);
    const crc = crc32(crcData);
    const crcBuffer = Buffer.alloc(4);
    crcBuffer.writeUInt32BE(crc >>> 0, 0);
    
    return Buffer.concat([length, typeBuffer, data, crcBuffer]);
  };
  
  const crc32 = (buf) => {
    let crc = -1;
    for (let i = 0; i < buf.length; i++) {
      crc = (crc >>> 8) ^ crcTable[(crc ^ buf[i]) & 0xFF];
    }
    return crc ^ -1;
  };
  
  const crcTable = [];
  for (let i = 0; i < 256; i++) {
    let c = i;
    for (let j = 0; j < 8; j++) {
      c = (c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1);
    }
    crcTable[i] = c;
  }
  
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(width, 0);
  ihdr.writeUInt32BE(height, 4);
  ihdr[8] = 8;
  ihdr[9] = 6;
  ihdr[10] = 0;
  ihdr[11] = 0;
  ihdr[12] = 0;
  
  const rawData = [];
  for (let y = 0; y < height; y++) {
    rawData.push(0);
    for (let x = 0; x < width; x++) {
      rawData.push(r, g, b, a);
    }
  }
  
  const { deflateSync } = require('zlib');
  const compressed = deflateSync(Buffer.from(rawData));
  
  const ihdrChunk = createChunk('IHDR', ihdr);
  const idatChunk = createChunk('IDAT', compressed);
  const iendChunk = createChunk('IEND', Buffer.alloc(0));
  
  return Buffer.concat([signature, ihdrChunk, idatChunk, iendChunk]);
};

const icons = [
  { name: 'energy.png', color: [148, 163, 184] },
  { name: 'energy-active.png', color: [24, 144, 255] },
  { name: 'staff.png', color: [148, 163, 184] },
  { name: 'staff-active.png', color: [24, 144, 255] },
  { name: 'boiler.png', color: [148, 163, 184] },
  { name: 'boiler-active.png', color: [24, 144, 255] }
];

if (!fs.existsSync(tabbarDir)) {
  fs.mkdirSync(tabbarDir, { recursive: true });
}

icons.forEach(icon => {
  const png = createPNG(48, 48, icon.color[0], icon.color[1], icon.color[2]);
  fs.writeFileSync(path.join(tabbarDir, icon.name), png);
  console.log(`Created: ${icon.name}`);
});

console.log('All icons generated successfully!');