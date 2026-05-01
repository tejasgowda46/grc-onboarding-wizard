# services/groq_service.py
import os
import json
import time
from groq import Groq
from dotenv import load_dotenv
from prompts.primary_prompt import get_primary_prompt
from prompts.recommend_prompt import get_recommend_prompt
from prompts.report_prompt import get_report_prompt
from services.health_service import record_response_time
from services.cache_service import get_from_cache, set_in_cache, make_cache_key

load_dotenv()

# Connect to Groq
client = Groq(api_key=os.getenv("GROQ_API_KEY"))


def call_groq(user_input: str) -> str:
    # Check cache first
    cache_key = make_cache_key("describe", user_input)
    cached = get_from_cache(cache_key)
    if cached:
        return cached

    start = time.time()
    response = client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "user", "content": get_primary_prompt(user_input)}
        ]
    )
    record_response_time((time.time() - start) * 1000)
    result = response.choices[0].message.content

    # Save to cache
    set_in_cache(cache_key, result)
    return result


def call_groq_recommend(user_input: str) -> list:
    # Check cache first
    cache_key = make_cache_key("recommend", user_input)
    cached = get_from_cache(cache_key)
    if cached:
        return cached

    start = time.time()
    response = client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "user", "content": get_recommend_prompt(user_input)}
        ]
    )
    record_response_time((time.time() - start) * 1000)

    raw = response.choices[0].message.content.strip()
    if raw.startswith("```"):
        raw = raw.split("```")[1]
        if raw.startswith("json"):
            raw = raw[4:]

    result = json.loads(raw)

    # Save to cache
    set_in_cache(cache_key, result)
    return result


def call_groq_report(user_input: str) -> dict:
    # Check cache first
    cache_key = make_cache_key("report", user_input)
    cached = get_from_cache(cache_key)
    if cached:
        return cached

    start = time.time()
    response = client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "user", "content": get_report_prompt(user_input)}
        ]
    )
    record_response_time((time.time() - start) * 1000)

    raw = response.choices[0].message.content.strip()
    if raw.startswith("```"):
        raw = raw.split("```")[1]
        if raw.startswith("json"):
            raw = raw[4:]

    result = json.loads(raw.strip())

    # Save to cache
    set_in_cache(cache_key, result)
    return result