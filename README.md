# 📱 MediaPlayerPlus - J2ME Multimedia Player 🎵🎬

J2ME MIDP 2.0 | MIT License | Version 3.4

> ✨ All-in-one multimedia player for Java phones (J2ME)  
> 📥 Download and play videos (3GP/MP4) and music (MP3) directly on your Nokia, Sony Ericsson, or Samsung!  
> 💎 Zero libVLC dependency — uses only your phone's native codecs via `javax.microedition.media`

┌──────────────────────────────────────────────────────────────┐
│  📱┌──────────────────────────────────────┐                 │
│    │  🎵 MediaPlayerPlus v3.4            │                 │
│    │  ┌────────────────────────────────┐ │                 │
│    │  │  ▶️ Play Video                 │ │                 │
│    │  │  🎧 Play Audio                 │ │                 │
│    │  │  📥 Download Video             │ │                 │
│    │  │  🎵 Download Audio             │ │                 │
│    │  │  📚 Video Library              │ │                 │
│    │  │  🎼 Audio Library              │ │                 │
│    │  │  ⚙️ Settings                   │ │                 │
│    │  └────────────────────────────────┘ │                 │
│    └──────────────────────────────────────┘                 │
└──────────────────────────────────────────────────────────────┘

## ✨ Complete Feature Set

### 📥 Intelligent Download System
- 🌐 YouTube → 3GP format (144p/240p/360p) compatible with 99% of J2ME phones
- 🎵 YouTube/TikTok/Facebook/Instagram → High-quality MP3 conversion
- 📡 Automatic codec detection based on your phone's capabilities
- 📶 Robust handling of slow connections (ED<img width="1024" height="1024" alt="icon" src="https://github.com/user-attachments/assets/9df8a161-eea2-4359-9190-a07511586ced" />
GE/GPRS networks)
- ⏳ Automatic resume after network interruptions
- 🔒 Offline mode: play previously downloaded files without internet
- 🔄 Smart fallback system when primary download method fails
- ⚡ Optimized memory usage during downloads (4KB buffer for old devices)

### 📚 Smart Library Management
- 📁 Automatic organization: Videos → "Videos" folder, Audio → "Audios" folder
- 🗑️ Safe deletion with confirmation dialogs
- 🔤 Alphabetical sorting of media files
- 📏 File size display in human-readable format (B/KB/MB)
- 🔄 Manual library refresh option
- 💾 Automatic detection of best storage location (phone memory vs memory card)
- 👁️ Hidden file filtering (ignores files starting with ".")
- 🔍 Support for multiple file extensions: .3gp, .mp4, .avi, .mp3, .wav, .aac, .amr

### 🎞️ Advanced Video Player
- ▶️ Intuitive controls: Play/Pause (Key 5), Back (Key 0), Hide UI (Key 7)
- 🔇 Volume control: Increase (Key 6), Decrease (Key 4)
- 👁️ Full-screen mode with automatic UI hiding after 3 seconds
- 🔄 Optional loop playback mode
- ⏱️ Real-time status display: "Playing", "Paused", "Completed"
- 📺 Native VideoControl integration for optimal display performance
- 🖼️ Fallback to audio-only playback when video codec unsupported
- 📱 Adapts to different screen sizes (128x128 up to 240x320)

### 🎧 Professional Audio Player
- 🌊 Real-time audio visualization with animated waveform bars
- ⏮️ Previous track (Key 1), ⏭️ Next track (Key 3)
- 📻 Automatic playlist creation from library selection
- 🔁 Configurable loop playback mode
- 📱 Smart filename truncation for small screens ("MyLongSongTitle..." → "MyLongSongTit...")
- 🎚️ Visual volume feedback with percentage display
- 🎧 Background playback support (when phone allows)
- 📊 Current track position display ("Track 3 of 12")

### 📊 Real-Time Progress Bar
- 📈 Smooth animation with bouncing download arrows
- 📊 Percentage display with large bold font (e.g., "67%")
- ⚡ Real-time speed calculation (e.g., "12 KB/s")
- ⏳ Accurate time remaining estimation (ETA)
- 📏 Dual size display: "downloaded / total" (e.g., "2.4 MB / 5.1 MB")
- 🎨 Modern color scheme: Deep blue (#1A1A2E) background with energetic pink (#E94560) progress bar
- 🌈 Gradient background animation for visual interest
- ⏱️ Automatic speed recalculation every second

### 🔁 Smart Conversion Engine
- 🔄 Audio extraction from 3GP/MP4 video files
- 🎵 Automatic conversion to MP3 format
- 💾 Smart filename generation (video title + ".mp3")
- ⚠️ Comprehensive error handling during conversion
- ✅ Visual success confirmation with file size display
- ⚡ Memory-efficient conversion using 4KB buffers
- 📁 Automatic saving to Audio Library folder

### ⚙️ Customizable Settings
- 🔊 Volume control (0-100% with real-time preview)
- 🔄 Auto-play toggle after download completion
- 🔁 Loop playback mode activation
- 💾 Storage path display showing detected root folders
- 📱 Supported format detection report (3GP/MP4/AAC)
- ⚙️ Reset settings to default option
- 🌐 Connection timeout configuration (hidden advanced setting)

## 📲 Verified Phone Compatibility ✅

### 🟢 Full Video + Audio Support (Recommended)
- 📱 Nokia: 2700 Classic, 2690, 3110c, 5310, 6300, N73, N95, N70, 6288
- 📱 Sony Ericsson: K800i, W910i, C902, W850i, K750i, W800i, K550i
- 📱 Samsung: GT-S3650 Corby, GT-S5230 Star, GT-C3300 Champ, SGH-F480 Tocco Lite
- 📱 Motorola: V8, V9, ZN5

### 🟡 Audio-Only Support (Video playback not possible)
- 📱 Nokia: 1680 Classic, 2630, 2760, 2680 Slide, 3500 Classic
- 📱 LG: KP500 Cookie, GM360 Viewty, KP202, KP220
- 📱 Samsung: SGH-E250, SGH-J700, SGH-G600
- 📱 BenQ-Siemens: EF81, S81

### 🔵 Limited Compatibility (Basic features only)
- 📱 Nokia: 1200, 1208, 1650, 2310 (audio playback only, no file storage access)
- 📱 Alcatel: OT-310, OT-501 (requires manual file placement)
- 📱 Siemens: C75, M75 (unstable video playback)

> 💡 Pro Tip: For maximum compatibility on older devices, ALWAYS select 144p or 240p quality with 3GP format. MP4 playback requires more advanced hardware found only in late-era J2ME phones (2007+).

## 📦 Step-by-Step Installation Guide

### Method 1: Bluetooth Transfer (Recommended)
1️⃣ Prepare both files on your computer:
   - `MediaPlayerPlusMIDlet.jar` (approximately 85 KB)
   - `MediaPlayerPlusMIDlet.jad` (approximately 0.5 KB)
2️⃣ Initiate Bluetooth transfer from computer to phone
3️⃣ Accept incoming file transfer on your phone
4️⃣ After transfer completes, open File Manager application
5️⃣ Navigate to "Received Files" or "Bluetooth" folder
6️⃣ Select and open the `.jad` file (NOT the .jar file)
7️⃣ Confirm installation prompts with "Yes" or "Install"
8️⃣ Wait for installation completion message
9️⃣ Launch application from main menu under "Applications" or "Games"

### Method 2: USB Cable Transfer
1️⃣ Connect phone to computer using USB cable
2️⃣ Select "Mass Storage Mode" or "File Transfer Mode" on phone
3️⃣ Computer will recognize phone as removable drive
4️⃣ Create folder named "MediaPlayerPlus" at root of drive
5️⃣ Copy both `.jar` and `.jad` files into this folder
6️⃣ Safely eject drive from computer
7️⃣ Disconnect USB cable from phone
8️⃣ Open File Manager on phone
9️⃣ Navigate to "MediaPlayerPlus" folder
🔟 Select `.jad` file and confirm installation
1️⃣1️⃣ Launch application after installation completes

### Method 3: Memory Card Installation (For restricted phones)
1️⃣ Remove memory card from phone
2️⃣ Insert into card reader connected to computer
3️⃣ Create folder "MediaPlayerPlus" at root of card
4️⃣ Inside, create subfolders: "Videos", "Audios", "Temp"
5️⃣ Copy `.jar` and `.jad` files into main "MediaPlayerPlus" folder
6️⃣ Safely eject card from computer
7️⃣ Reinsert card into phone
8️⃣ Install file manager app if not present (e.g., "X-Plore File Manager")
9️⃣ Use file manager to navigate to memory card → MediaPlayerPlus folder
🔟 Open `.jad` file and install application
1️⃣1️⃣ Application will now appear in phone's main menu

> ⚠️ Important: Some carriers block 3rd-party app installation. If installation fails:
> - 🔁 Restart phone completely (power off → wait 10 seconds → power on)
> - ⚙️ Check security settings: Menu → Settings → Application Manager → "Allow unsigned apps"
> - 🔄 Try renaming `.jar` to `.mp3` during transfer, then rename back to `.jar` on phone before installation
> - 📶 Ensure at least 200 KB free space in phone memory for installation

## 🌐 Download System Architecture

### 🔑 Integrated APIs (2026 Verified)
- 🌐 loader.to service: YouTube → 3GP/MP3 conversion without API keys
- 📡 Automatic format selection based on phone capabilities:
  * 144p → Format ID 17 (3GP, baseline profile)
  * 240p → Format ID 36 (3GP, extended profile)
  * 360p → Format ID 18 (MP4, if supported)
- 🔄 Four-layer fallback system:
  Layer 1: Primary loader.to endpoint with format detection
  Layer 2: Alternative loader.to endpoint with forced 3GP output
  Layer 3: Direct format 17/36 URL construction
  Layer 4: User-provided direct links (.3gp/.mp3 files)
- 📶 Connection resilience:
  * 30-second read timeout to prevent hangs
  * Automatic retry on connection reset
  * Chunked download with progress updates every 40KB
  * Memory-safe streaming (no full-file buffering)

### 💾 File Storage System
On first launch, application automatically detects optimal storage location:
```
Memory card detected → E:/MediaPlayerPlus/
Phone memory only → C:/MediaPlayerPlus/

Automatic folder structure:
├── Videos/    → Stores .3gp, .mp4, .avi files
├── Audios/    → Stores .mp3, .wav, .aac, .amr files
└── Temp/      → Temporary download files (auto-cleaned)
```

> 💡 User Tip: You can manually copy media files to these folders via USB cable. Application will detect them automatically when you open the Library screens. Supported filename characters: letters, numbers, underscore, hyphen. Avoid special characters (!@#$%) for best compatibility.

## ❓ Comprehensive FAQ

### ❓ "Why do I see 'libVLC required' message on my computer?"
✅ Explanation: This message appears ONLY when trying to open the .jar file on a PC using VLC Media Player. **J2ME applications NEVER use libVLC** — they rely exclusively on the phone's built-in media engine via `javax.microedition.media.Manager`. This is a desktop software misunderstanding. Simply transfer the files to your J2ME phone and install normally — the message will never appear on the actual device.

### ❓ "Video won't play — what should I try?"
✅ Step-by-step troubleshooting:
1️⃣ Always select 144p or 240p quality with 3GP format for older phones
2️⃣ Completely restart phone (power off → wait 15 seconds → power on)
3️⃣ Verify free storage space: minimum 5 MB required in target folder
4️⃣ Try downloading a small test video first (under 1 MB)
5️⃣ Attempt playback of a known-good 3GP file copied manually via USB
6️⃣ Check phone specifications: must support "MPEG-4 SP" or "H.263" video codec
7️⃣ If all fails: use audio-only mode which works on 100% of MIDP 2.0 phones

### ❓ "Where are my downloaded files saved?"
✅ Detailed answer: Application creates "MediaPlayerPlus" folder at root of best available storage:
- Nokia S40 phones: Usually `E:/MediaPlayerPlus/` (memory card) or `C:/MediaPlayerPlus/` (phone memory)
- Sony Ericsson: `Memory Stick:/MediaPlayerPlus/`
- Samsung: `SD Card:/MediaPlayerPlus/` or `Phone:/MediaPlayerPlus/`
- LG: `SD Card:/MediaPlayerPlus/`

To find exact path: Open app → Settings → View "Storage" section showing detected paths. You can also check using your phone's native file manager application.

### ❓ "Why do downloads sometimes fail?"
✅ Common causes and solutions:
- 📶 Weak signal area: Move near window or higher ground → retry download
- 🔗 API temporary outage: Wait 1 hour → try again OR use direct .mp3/.3gp link
- 💾 Insufficient space: Delete old files → free at least 10 MB → retry
- 🌐 Carrier restrictions: Some mobile operators block 3rd-party download services → try on Wi-Fi if available OR use direct links
- ⏱️ Timeout on slow networks: Select 144p quality (smallest file size) → retry
- 🔒 Security software: Disable "download protection" in phone security settings

### ❓ "Can I use this app without internet?"
✅ Absolutely! Full offline functionality includes:
- 🔊 Playing all previously downloaded audio files
- 🎞️ Playing all previously downloaded video files
- 📚 Browsing and managing your media library
- 🗑️ Deleting unwanted files
- ⚙️ Changing all settings (volume, loop mode, etc.)
- 🔁 Converting videos to audio (if source video exists locally)

Internet connection is ONLY required for new download operations. Once media is saved to your phone, it works completely offline — perfect for travel or areas with poor coverage.

## 🛠️ Quick Troubleshooting Guide

| Symptom | Immediate Solution |
|---------|-------------------|
| ❌ App crashes on startup | 🔁 Full phone restart → Reinstall app → Try again |
| ⏳ Download stuck at 0% | 📶 Check signal strength → Switch to 144p quality → Retry |
| 🔇 No sound during playback | 🔉 Increase phone volume → Press Key 6 in player → Check mute status |
| 📵 "File system not available" | ⚠️ Your phone lacks JSR-75 support → Use direct download to phone memory only |
| 🔄 Infinite menu loop | 🚫 Force-close app via task manager → Restart phone → Reopen app |
| 🖼️ Black screen during video | 👁️ Press Key 7 to toggle controls → Check if video codec supported → Try 3GP format |
| ⚡ Battery drains fast | 🔋 Close app completely after use → Disable auto-play → Reduce screen brightness |
| 📶 Slow downloads | ⏬ Select 144p quality → Move to better signal area → Avoid peak hours |

## 🌍 Project Philosophy

MediaPlayerPlus was born from passion for the J2ME ecosystem and desire to breathe new life into the millions of functional Java phones still in use worldwide. Unlike modern resource-hungry applications:

✨ We prioritize extreme lightness (just 85 KB!)  
✨ We respect battery life (zero background processes)  
✨ We work without user accounts or tracking  
✨ We use 100% standard J2ME APIs (no dangerous hacks)  
✨ We protect privacy (zero data collection)  
✨ We embrace constraints as creative opportunities  

This is a love letter to the golden age of mobile — when an entire application fit in 100 KB and ran on virtually any device. We believe technology should serve people, not the other way around. Your decade-old Nokia deserves dignity and functionality in 2026 and beyond.

## 🤝 How to Contribute

Contributions welcome even without J2ME experience! You can help by:

🔧 Development:
- Adding alternative download APIs as loader.to fallbacks
- Improving codec detection for obscure phone models
- Optimizing memory usage for ultra-low-end devices (<1MB free RAM)
- Fixing bugs reported in GitHub Issues
- Adding support for additional audio formats (AMR-WB, MIDI)

🌍 Documentation:
- Testing on your specific phone model and reporting results
- Creating model-specific installation guides
- Translating interface strings to new languages
- Documenting carrier-specific restrictions by country

📣 Community:
- Answering newcomer questions on J2ME forums
- Sharing usage tips on retro mobile communities
- Creating text-based tutorials (no images required)
- Organizing "J2ME revival" events in your region

> 💬 Join the conversation: Open a GitHub Issue with label "j2me-help" for any question — we respond within 48 hours!

## 📜 MIT License

Copyright © 2026 David

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## 💫 Special Thanks

🙏 To Sun Microsystems engineers who created the elegant J2ME platform  
🙏 To Nokia designers who made Java phones accessible to billions  
🙏 To XDA Developers community preserving WTK tools and knowledge  
🙏 To feature phone users worldwide keeping J2ME alive in 2026+  
🙏 To retro mobile collectors preserving mobile history  
🙏 To developers maintaining loader.to and other J2ME-friendly services  
🙏 To every person who believes simple technology can still change lives  

---
YouTube 🌐 : DASH ANIMATION V2 link: https://www.youtube.com/@dash______animationv2
Telegram 📲 : Java game uploader 240x320 link: https://t.me/javagameuploader240_320
Email 🖨️ : ndukadavid70@gmail.com
Phone 📞 : +225 0788463112
---

📱 Crafted with passion for phones that connected our world before smartphones existed  
🎵 Because music and video deserve to be accessible on EVERY device  
💎 Because an 85 KB file can still bring joy in our gigabyte-obsessed world  

✨ Thank you for using MediaPlayerPlus — The J2ME spirit lives on! ✨
<img width="1024" height="1024" alt="icon" src="https://github.com/user-attachments/assets/88acfc47-34ee-43fe-a758-7a94b0d3af41" />
