export interface EditorJsBlock {
  type: string;
  data: any;
}

export interface ParsedEditorJs {
  blocks: EditorJsBlock[];
}

export function parseEditorJsContent(content: string): ParsedEditorJs {
  try {
    const parsed = JSON.parse(content);
        const blocks: EditorJsBlock[] = Array.isArray(parsed)
      ? parsed
      : parsed?.blocks ?? [];
    return { blocks };
  } catch (e) {
    console.error('Error parsing EditorJS content', e);
    return { blocks: [] };
  }
}
