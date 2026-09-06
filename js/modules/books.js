/**
 * وحدة الكتب والمصادر الإبيغرافية (Books & Sources Module)
 * مكوّن مستقل تماماً لإدارة وفحص وعرض الكتب والمصادر وسجلات التوثيق.
 */

export class BooksManager {
  constructor(sourcesUrl = 'data/sources.json') {
    this.sourcesUrl = sourcesUrl;
    this.sources = [];
    this.selectedBook = null;
    this.listeners = new Set();
  }

  /**
   * تحميل سجلات المصادر من ملف sources.json
   */
  async loadSources() {
    try {
      const response = await fetch(this.sourcesUrl);
      if (!response.ok) {
        throw new Error(`فشل تحميل المصادر: ${response.statusText}`);
      }
      this.sources = await response.json();
      return this.sources;
    } catch (error) {
      console.error('BooksManager: خطأ أثناء قراءة sources.json', error);
      // استخدام بيانات احتياطية فورية في حال عدم توفر fetch المباشر في بيئة محلية
      if (window.__SEMITIC_SOURCES_DATA__) {
        this.sources = window.__SEMITIC_SOURCES_DATA__;
        return this.sources;
      }
      return [];
    }
  }

  /**
   * استرجاع جميع سجلات المصادر
   */
  getAllSources() {
    return this.sources;
  }

  /**
   * البحث في الكتب والمصادر بواسطة الكلمات المفتاحية
   */
  searchSources(query = '') {
    const q = query.trim().toLowerCase();
    if (!q) return this.sources;

    return this.sources.filter(source => {
      const matchTitle = (source.title || '').toLowerCase().includes(q) || (source.titleAr || '').toLowerCase().includes(q);
      const matchAuthor = (source.author || '').toLowerCase().includes(q);
      const matchYear = (source.publicationYear || '').toLowerCase().includes(q);
      const matchLang = (source.language || '').toLowerCase().includes(q);
      const matchFile = (source.originalFileName || '').toLowerCase().includes(q);
      const matchRelLangs = (source.relatedLanguages || []).some(l => l.toLowerCase().includes(q));
      const matchRelInsc = (source.relatedInscriptions || []).some(i => i.toLowerCase().includes(q));

      return matchTitle || matchAuthor || matchYear || matchLang || matchFile || matchRelLangs || matchRelInsc;
    });
  }

  /**
   * جلب كتاب بواسطة المعرّف
   */
  getSourceById(id) {
    return this.sources.find(s => s.id === id) || null;
  }

  /**
   * صياغة سجل المصدر مع الالتزام التام ببيان "غير معروف" للقيم غير المتوفرة
   */
  formatSourceRecord(source) {
    if (!source) return null;
    return {
      id: source.id,
      originalFileName: source.originalFileName || 'غير معروف',
      title: source.title || 'غير معروف',
      titleAr: source.titleAr || source.title || 'غير معروف',
      author: source.author && source.author.trim() ? source.author : 'غير معروف',
      publicationYear: source.publicationYear && source.publicationYear.trim() ? source.publicationYear : 'غير معروف',
      language: source.language && source.language.trim() ? source.language : 'غير معروف',
      extractionDate: source.extractionDate || '2026-09-02',
      sourceType: source.sourceType || 'غير معروف',
      filePath: source.filePath || '',
      summary: source.summary || 'غير معروف',
      relatedLanguages: source.relatedLanguages || [],
      relatedInscriptions: source.relatedInscriptions || [],
      notes: source.notes || ''
    };
  }

  /**
   * إنشاء كود عارض الـ PDF الداخلي داخل حاوية مخصصة مع رابط احتياطي
   */
  renderPdfViewer(containerElement, source, options = {}) {
    if (!containerElement || !source) return;

    const formatted = this.formatSourceRecord(source);
    const pdfPath = formatted.filePath;

    containerElement.innerHTML = `
      <div class="pdf-viewer-card" style="display: flex; flex-direction: column; height: 100%; border: 1px solid rgba(255,255,255,0.12); border-radius: 12px; overflow: hidden; background: #1a1614; color: #ede0d4;">
        <!-- شريط تحكم عارض الـ PDF -->
        <div class="pdf-viewer-header" style="display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; background: #261f1c; border-bottom: 1px solid rgba(255,255,255,0.08); flex-wrap: wrap; gap: 8px;">
          <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 1.4rem;">📖</span>
            <div>
              <div style="font-weight: bold; font-size: 0.95rem; color: #e6c594;">${formatted.titleAr}</div>
              <div style="font-size: 0.75rem; color: #a89f91;">ملف: <code style="background: rgba(0,0,0,0.3); padding: 2px 6px; border-radius: 4px;">${formatted.originalFileName}</code> | المؤلف: ${formatted.author}</div>
            </div>
          </div>
          
          <div style="display: flex; gap: 8px; align-items: center;">
            <a href="${pdfPath}" target="_blank" rel="noopener noreferrer" 
               class="btn-fallback-tab"
               style="display: inline-flex; align-items: center; gap: 6px; padding: 6px 12px; background: #8c5836; color: #fff; text-decoration: none; border-radius: 6px; font-size: 0.8rem; font-weight: bold; transition: background 0.2s;"
               title="فتح في تبويب مستقل">
              <span>↗️ فتح في تبويب جديد</span>
            </a>
          </div>
        </div>

        <!-- تفاصيل سجل المصدر المدمج (Source Record Metadata) -->
        <div class="pdf-source-meta" style="padding: 10px 16px; background: #201a17; border-bottom: 1px solid rgba(255,255,255,0.06); font-size: 0.8rem; display: flex; flex-wrap: wrap; gap: 12px; color: #c4b5a5;">
          <div><strong style="color: #d4a373;">سنة النشر:</strong> ${formatted.publicationYear}</div>
          <div><strong style="color: #d4a373;">اللغة:</strong> ${formatted.language}</div>
          <div><strong style="color: #d4a373;">تاريخ الاستخراج:</strong> ${formatted.extractionDate}</div>
          <div><strong style="color: #d4a373;">النقوش المرتبطة:</strong> ${formatted.relatedInscriptions.length ? formatted.relatedInscriptions.join('، ') : 'لا يوجد'}</div>
        </div>

        <!-- منطقة العرض التفاعلية المباشرة (Embedded Frame) -->
        <div class="pdf-frame-wrapper" style="flex: 1; position: relative; min-height: 450px; background: #2b2b2b;">
          <iframe 
            src="${pdfPath}#toolbar=1&navpanes=1" 
            style="width: 100%; height: 100%; min-height: 450px; border: none;"
            title="${formatted.title}">
            <p style="padding: 20px; text-align: center; color: #ccc;">
              متصفحك لا يدعم عرض ملفات PDF مباشرة هنا. 
              <a href="${pdfPath}" target="_blank" style="color: #e6c594; text-decoration: underline;">اضغط هنا لتحميل أو فتح الكتاب في تبويب جديد</a>.
            </p>
          </iframe>
        </div>
      </div>
    `;
  }
}

// تصدير افتراضي للوحدة
export default BooksManager;
