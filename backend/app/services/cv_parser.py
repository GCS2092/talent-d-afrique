from pathlib import Path

import pdfplumber
import pytesseract
from pdf2image import convert_from_path

from app.core.skills_dictionary import extract_skills_from_text

# Seuil en dessous duquel on considere qu'un PDF est probablement scanne
# (peu de texte extrait directement = probablement une image, pas du vrai texte).
MIN_TEXT_LENGTH_BEFORE_OCR = 50


def extract_text_from_pdf(file_path: str) -> str:
    """Extrait le texte d'un PDF. Si le texte est trop court (PDF scanne),
    bascule automatiquement sur de l'OCR (reconnaissance d'image)."""
    text = ""

    with pdfplumber.open(file_path) as pdf:
        for page in pdf.pages:
            page_text = page.extract_text()
            if page_text:
                text += page_text + "\n"

    if len(text.strip()) < MIN_TEXT_LENGTH_BEFORE_OCR:
        text = _extract_text_with_ocr(file_path)

    return text.strip()


def _extract_text_with_ocr(file_path: str) -> str:
    """Fallback pour les CV scannes : convertit chaque page en image puis lit le texte."""
    images = convert_from_path(file_path)
    text = ""

    for image in images:
        text += pytesseract.image_to_string(image, lang="fra") + "\n"

    return text.strip()


def parse_cv(file_path: str) -> dict:
    """Analyse un CV et retourne le texte brut et les competences detectees."""
    raw_text = extract_text_from_pdf(file_path)
    skills = extract_skills_from_text(raw_text)

    return {
        "raw_text": raw_text,
        "competences_detectees": skills,
    }


def ensure_storage_dir(directory: str) -> Path:
    path = Path(directory)
    path.mkdir(parents=True, exist_ok=True)
    return path
